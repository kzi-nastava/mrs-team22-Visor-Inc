package inc.visor.voom_service.ride.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.repository.UserRepository;
import inc.visor.voom_service.driver.dto.DriverSummaryDto;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.osrm.dto.DriverAssignedDto;
import inc.visor.voom_service.osrm.service.RideWsService;
import inc.visor.voom_service.ride.dto.RideRequestCreateDTO;
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
import inc.visor.voom_service.simulation.Simulator;
import inc.visor.voom_service.vehicle.model.VehicleType;
import inc.visor.voom_service.vehicle.repository.VehicleTypeRepository;

@Service
public class RideRequestService {

    private final RideRepository rideRepository;
    private final RideRequestRepository rideRequestRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final RideEstimateService rideEstimationService;
    private final UserRepository userRepository;
    private final DriverService driverService;
    private final RideWsService rideWsService;
    private final Simulator driverSimulator;

    public RideRequestService(
            RideRequestRepository rideRequestRepository,
            VehicleTypeRepository vehicleTypeRepository,
            RideEstimateService rideEstimationService,
            UserRepository userRepository,
            DriverService driverService,
            RideRepository rideRepository,
            RideWsService rideWsService,
            Simulator driverSimulator
    ) {
        this.rideRequestRepository = rideRequestRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.rideEstimationService = rideEstimationService;
        this.userRepository = userRepository;
        this.driverService = driverService;
        this.rideRepository = rideRepository;
        this.rideWsService = rideWsService;
        this.driverSimulator = driverSimulator;
    }

    public RideRequestResponseDto createRideRequest(
            RideRequestCreateDTO dto,
            Long userId
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        VehicleType vehicleType
                = vehicleTypeRepository.findById(dto.vehicleTypeId)
                        .orElseThrow(()
                                -> new IllegalArgumentException("Invalid vehicle type")
                        );

        RideEstimationResult estimate
                = rideEstimationService.estimate(dto, vehicleType);

        RideRequest rideRequest
                = RideRequestMapper.toEntity(
                        dto,
                        user,
                        vehicleType,
                        estimate.price(),
                        estimate.distanceKm()
                );

        Driver driver = driverService.findDriverForRideRequest(rideRequest, dto.getFreeDriversSnapshot());

        boolean driverFound = driver != null;

        if (driverFound) {
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
            userRepository.findByEmail(email).ifPresent(passengers::add);
        }
        ride.setPassengers(passengers);

        
        if (driverFound && rideRequest.getScheduleType() == ScheduleType.NOW) {
        DriverAssignedDto driverAssignedDto = new DriverAssignedDto(ride.getId(), driver.getId(), rideRequest.getRideRoute().getRoutePoints());
            rideRepository.save(ride);
            rideWsService.sendDriverAssigned(driverAssignedDto);
        
            driverSimulator.changeDriverRoute(driver.getId(), rideRequest.getRideRoute().getRoutePoints().getFirst().getLatitude(), rideRequest.getRideRoute().getRoutePoints().getFirst().getLongitude());
        } else if (driverFound && rideRequest.getScheduleType() == ScheduleType.LATER) {
            rideRepository.save(ride);
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
