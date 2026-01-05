package inc.visor.voom_service.driver.service;

import org.springframework.stereotype.Service;

import inc.visor.voom_service.driver.dto.DriverLocationDto;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.repository.DriverRepository;
import inc.visor.voom_service.vehicle.dto.VehicleSummaryDto;
import inc.visor.voom_service.vehicle.model.Vehicle;
import inc.visor.voom_service.vehicle.repository.VehicleRepository;

@Service
public class DriverService {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    public DriverService(VehicleRepository vehicleRepository, DriverRepository driverRepository) {
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
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

}
