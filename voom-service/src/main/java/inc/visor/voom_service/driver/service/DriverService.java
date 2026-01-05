package inc.visor.voom_service.driver.service;

import org.springframework.stereotype.Service;

import inc.visor.voom_service.driver.dto.DriverLocationDto;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.repository.DriverRepository;
import inc.visor.voom_service.vehicle.dto.VehicleSummaryDto;
import inc.visor.voom_service.vehicle.model.Vehicle;
import inc.visor.voom_service.vehicle.model.VehicleType;
import inc.visor.voom_service.vehicle.repository.VehicleRepository;
import inc.visor.voom_service.vehicle.repository.VehicleTypeRepository;

@Service
public class DriverService {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final VehicleTypeRepository vehicleTypeRepository;

    public DriverService(VehicleRepository vehicleRepository, DriverRepository driverRepository, VehicleTypeRepository vehicleTypeRepository) {
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
    }

    public void simulateMove(DriverLocationDto dto) {
        // used for simulating movement
        // one possible way is to 1. select 2 random points in Novi Sad
        // 2. call external api to get route and waypoints between these two
        // 3. update position for each waypoint that api returns
        // 4. broadcast update
        return;
    }

    public void reportDriver(Long driverId, Long userId, String comment) {
        return;
    }

    public VehicleSummaryDto getVehicle(Long userId) {

        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Driver not found"));

        Vehicle vehicle = vehicleRepository.findByDriverId(driver.getId())
                .orElseThrow(() -> new IllegalStateException("Vehicle not found"));

        VehicleSummaryDto dto = new VehicleSummaryDto(
                vehicle.getVehicleType().getType(),
                vehicle.getYear(),
                vehicle.getModel(),
                vehicle.getLicensePlate(),
                vehicle.isBabySeat(),
                vehicle.isPetFriendly(),
                vehicle.getNumberOfSeats()
        );

        return dto;
    }

    public VehicleSummaryDto updateVehicle(Long userId, VehicleSummaryDto request) {

        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Driver not found"));

        Vehicle vehicle = vehicleRepository.findByDriverId(driver.getId())
                .orElseThrow(() -> new IllegalStateException("Vehicle not found"));

        VehicleType vehicleType
                = vehicleTypeRepository.findByType(request.getVehicleType())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid vehicle type"));

        vehicle.setModel(request.getModel());
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setNumberOfSeats(request.getNumberOfSeats());
        vehicle.setBabySeat(request.isBabySeat());
        vehicle.setPetFriendly(request.isPetFriendly());
        vehicle.setVehicleType(vehicleType);

        vehicleRepository.save(vehicle);

        VehicleSummaryDto dto = new VehicleSummaryDto(
                vehicle.getVehicleType().getType(),
                vehicle.getYear(),
                vehicle.getModel(),
                vehicle.getLicensePlate(),
                vehicle.isBabySeat(),
                vehicle.isPetFriendly(),
                vehicle.getNumberOfSeats()
        );

        return dto;
    }

}
