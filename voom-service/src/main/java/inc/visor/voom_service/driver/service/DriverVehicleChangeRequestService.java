package inc.visor.voom_service.driver.service;

import inc.visor.voom_service.driver.dto.DriverVehicleChangeRequestDto;
import inc.visor.voom_service.driver.dto.VehicleChangeRequestStatus;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.DriverVehicleChangeRequest;
import inc.visor.voom_service.driver.repository.DriverVehicleChangeRequestRepository;
import inc.visor.voom_service.vehicle.model.Vehicle;
import inc.visor.voom_service.vehicle.model.VehicleType;
import inc.visor.voom_service.vehicle.repository.VehicleRepository;
import inc.visor.voom_service.vehicle.repository.VehicleTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverVehicleChangeRequestService {

    private final DriverVehicleChangeRequestRepository changeRequestRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleTypeRepository vehicleTypeRepository;

    public DriverVehicleChangeRequestDto getById(Long id) {

        DriverVehicleChangeRequest request = changeRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        return mapToDto(request);
    }

    public List<DriverVehicleChangeRequestDto> getAllPending() {

        return changeRequestRepository
                .findByStatus(VehicleChangeRequestStatus.PENDING)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public void approve(Long id) {

        DriverVehicleChangeRequest request = changeRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (request.getStatus() != VehicleChangeRequestStatus.PENDING) {
            throw new IllegalStateException("Request already processed.");
        }

        Driver driver = request.getDriver();

        Vehicle vehicle = vehicleRepository.findByDriverId(driver.getId())
                .orElseThrow(() -> new IllegalStateException("Vehicle not found"));

        VehicleType vehicleType = vehicleTypeRepository
                .findByType(request.getVehicleType())
                .orElseThrow(() -> new IllegalArgumentException("Invalid vehicle type"));

        vehicle.setModel(request.getModel());
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setNumberOfSeats(request.getNumberOfSeats());
        vehicle.setBabySeat(request.isBabySeat());
        vehicle.setPetFriendly(request.isPetFriendly());
        vehicle.setVehicleType(vehicleType);

        vehicleRepository.save(vehicle);

        request.setStatus(VehicleChangeRequestStatus.APPROVED);
        request.setReviewedAt(LocalDateTime.now());

        changeRequestRepository.save(request);
    }

    public void reject(Long id) {

        DriverVehicleChangeRequest request = changeRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (request.getStatus() != VehicleChangeRequestStatus.PENDING) {
            throw new IllegalStateException("Request already processed.");
        }

        request.setStatus(VehicleChangeRequestStatus.REJECTED);
        request.setReviewedAt(LocalDateTime.now());

        changeRequestRepository.save(request);
    }

    private DriverVehicleChangeRequestDto mapToDto(DriverVehicleChangeRequest r) {

        String fullName = r.getDriver().getUser().getPerson().getFirstName()
                + " " +
                r.getDriver().getUser().getPerson().getLastName();

        return new DriverVehicleChangeRequestDto(
                r.getId(),
                r.getDriver().getId(),
                fullName,
                r.getModel(),
                r.getVehicleType(),
                r.getLicensePlate(),
                r.getNumberOfSeats(),
                r.isBabySeat(),
                r.isPetFriendly(),
                r.getStatus().name(),
                r.getCreatedAt()
        );
    }
}
