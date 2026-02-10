package inc.visor.voom_service.ride.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.model.UserStatus;
import inc.visor.voom_service.auth.user.service.UserService;
import inc.visor.voom_service.driver.dto.DriverSummaryDto;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.DriverStatus;
import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.exception.AccessDeniedException;
import inc.visor.voom_service.exception.NotFoundException;
import inc.visor.voom_service.exception.RideScheduleTooLateException;
import inc.visor.voom_service.osrm.dto.DriverAssignedDto;
import inc.visor.voom_service.osrm.service.RideWsService;
import inc.visor.voom_service.ride.dto.RideRequestCreateDto;
import inc.visor.voom_service.ride.dto.RideRequestResponseDto;
import inc.visor.voom_service.ride.mapper.RideRequestMapper;
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.model.RideEstimationResult;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.enums.RideRequestStatus;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import inc.visor.voom_service.ride.model.enums.ScheduleType;
import inc.visor.voom_service.ride.repository.RideRepository;
import inc.visor.voom_service.ride.repository.RideRequestRepository;
import inc.visor.voom_service.shared.notification.model.enums.NotificationType;
import inc.visor.voom_service.shared.notification.service.NotificationService;
import inc.visor.voom_service.simulation.Simulator;
import inc.visor.voom_service.vehicle.model.VehicleType;
import inc.visor.voom_service.vehicle.service.VehicleTypeService;

@Service
public class RideRequestService {

    private final RideRequestRepository rideRequestRepository;
    private final RideEstimateService rideEstimationService;
    private final DriverService driverService;
    private final RideWsService rideWsService;
    private final Simulator driverSimulator;
    private final UserService userService;
    private final VehicleTypeService vehicleTypeService;
    private final RideService rideService;
    private final RideRepository rideRepository;
    private final NotificationService notificationService;

    public RideRequestService(
            RideRequestRepository rideRequestRepository,
            RideEstimateService rideEstimationService,
            DriverService driverService,
            RideWsService rideWsService,
            NotificationService notificationService,
            Simulator driverSimulator, UserService userService, VehicleTypeService vehicleTypeService, RideService rideService, RideRepository rideRepository
    ) {
        this.rideRequestRepository = rideRequestRepository;
        this.rideEstimationService = rideEstimationService;
        this.driverService = driverService;
        this.rideService = rideService;
        this.rideWsService = rideWsService;
        this.driverSimulator = driverSimulator;
        this.userService = userService;
        this.vehicleTypeService = vehicleTypeService;
        this.rideRepository = rideRepository;
        this.notificationService = notificationService;
    }

    public RideRequest update(RideRequest rideRequest) {
        return this.rideRequestRepository.save(rideRequest);
    }

    public RideRequestResponseDto createRideRequest(RideRequestCreateDto dto, Long userId) {
        User user = this.userService.getUser(userId).orElseThrow(NotFoundException::new);
        VehicleType vehicleType = this.vehicleTypeService.getVehicleType(dto.vehicleTypeId).orElseThrow(NotFoundException::new);
        RideEstimationResult estimate = rideEstimationService.estimate(dto.route.points, vehicleType);

        if (user.getUserStatus() == UserStatus.SUSPENDED) {
            throw new AccessDeniedException();
        }

        RideRequest rideRequest = RideRequestMapper.toEntity(
                dto,
                user,
                vehicleType,
                estimate.price(),
                estimate.distanceKm()
        );

        Driver driver = driverService.findDriverForRideRequest(rideRequest, dto.getFreeDriversSnapshot());

        boolean driverFound = driver != null;

        if (dto.schedule.type.equals("LATER")
                && dto.schedule.startAt.isAfter(Instant.now().plus(5, ChronoUnit.HOURS))) {
            throw new RideScheduleTooLateException();
        }

        if (driverFound && rideRequest.getScheduleType() == ScheduleType.NOW) {
            driver.setStatus(DriverStatus.BUSY);
            driver = driverService.updateDriver(driver);
            rideRequest.setStatus(RideRequestStatus.ACCEPTED);
        } else if (driverFound && rideRequest.getScheduleType() == ScheduleType.LATER) {
            rideRequest.setStatus(RideRequestStatus.ACCEPTED);
        } else {
            rideRequest.setStatus(RideRequestStatus.REJECTED);
        }

        rideRequestRepository.save(rideRequest);

        Ride ride = new Ride();
        ride.setRideRequest(rideRequest);
        ride.setStatus(
                rideRequest.getScheduleType() == ScheduleType.LATER
                ? RideStatus.SCHEDULED
                : RideStatus.ONGOING
        );
        ride.setDriver(driver);

        List<User> passengers = new ArrayList<>();
        for (String email : rideRequest.getLinkedPassengerEmails()) {
            this.userService.getUser(email).ifPresent(passengers::add);
        }
        ride.setPassengers(passengers);

        if (driverFound && rideRequest.getScheduleType() == ScheduleType.NOW) {
            this.rideService.save(ride);
            notificationService.createAndSendNotification(
                    driver.getUser(),
                    NotificationType.RIDE_ASSIGNED,
                    "New ride assigned 🚖",
                    "You have been assigned a new ride.",
                    ride.getId()
            );

            notificationService.createAndSendNotification(
                    user,
                    NotificationType.RIDE_ACCEPTED,
                    "Ride confirmed",
                    "Your driver is on the way. Ride will start shortly.",
                    ride.getId()
            );

            DriverAssignedDto driverAssignedDto = new DriverAssignedDto(ride.getId(), driver.getId(), rideRequest.getRideRoute().getRoutePoints());
            System.out.println("Sending driver assigned via WS: " + driverAssignedDto);
            rideWsService.sendDriverAssigned(driverAssignedDto);

            driverSimulator.changeDriverRoute(driver.getId(), rideRequest.getRideRoute().getRoutePoints().getFirst().getLatitude(), rideRequest.getRideRoute().getRoutePoints().getFirst().getLongitude());
        } else if (driverFound && rideRequest.getScheduleType() == ScheduleType.LATER) {
            this.rideService.save(ride);
            notificationService.createAndSendNotification(
                    user,
                    NotificationType.RIDE_ACCEPTED,
                    "Ride scheduled",
                    "Your ride has been scheduled successfully.",
                    ride.getId()
            );

        }

        return RideRequestResponseDto.from(
                rideRequest,
                estimate.distanceKm(),
                driverFound
                        ? new DriverSummaryDto(driver)
                        : null
        );
    }
}
