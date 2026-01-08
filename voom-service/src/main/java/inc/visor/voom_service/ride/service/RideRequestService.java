package inc.visor.voom_service.ride.service;

import org.springframework.stereotype.Service;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.repository.UserRepository;
import inc.visor.voom_service.driver.dto.DriverSummaryDto;
import inc.visor.voom_service.ride.dto.RideRequestCreateDTO;
import inc.visor.voom_service.ride.dto.RideRequestResponseDto;
import inc.visor.voom_service.ride.mapper.RideRequestMapper;
import inc.visor.voom_service.ride.model.RideEstimationResult;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.enums.RideRequestStatus;
import inc.visor.voom_service.ride.repository.RideRequestRepository;
import inc.visor.voom_service.vehicle.model.VehicleType;
import inc.visor.voom_service.vehicle.repository.VehicleTypeRepository;

@Service
public class RideRequestService {

    private final RideRequestRepository rideRequestRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final RideEstimateService rideEstimationService;
    private final UserRepository userRepository;

    public RideRequestService(
        RideRequestRepository rideRequestRepository,
        VehicleTypeRepository vehicleTypeRepository,
        RideEstimateService rideEstimationService,
        UserRepository userRepository
    ) {
        this.rideRequestRepository = rideRequestRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.rideEstimationService = rideEstimationService;
        this.userRepository = userRepository;
    }

    public RideRequestResponseDto createRideRequest(
        RideRequestCreateDTO dto,
        Long userId
    ) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalStateException("User not found"));


        VehicleType vehicleType =
            vehicleTypeRepository.findById(dto.vehicleTypeId)
                .orElseThrow(() ->
                    new IllegalArgumentException("Invalid vehicle type")
                );

        RideEstimationResult estimate =
            rideEstimationService.estimate(dto, vehicleType);

        RideRequest rideRequest =
            RideRequestMapper.toEntity(
                dto,
                user,
                vehicleType,
                estimate.price(),
                estimate.distanceKm()
            );

        boolean driverFound = true;

        if (driverFound) {
            rideRequest.setStatus(RideRequestStatus.ACCEPTED);
        } else {
            rideRequest.setStatus(RideRequestStatus.REJECTED);
        }

        rideRequestRepository.save(rideRequest);

        return RideRequestResponseDto.from(
            rideRequest,
            estimate.distanceKm(),
            driverFound
                ? new DriverSummaryDto(1L, "John", "Doe")
                : null
        );
    }
}
