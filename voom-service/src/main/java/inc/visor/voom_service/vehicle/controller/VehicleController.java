package inc.visor.voom_service.vehicle.controller;

import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.exception.NotFoundException;
import inc.visor.voom_service.vehicle.dto.CreateVehicleDto;
import inc.visor.voom_service.vehicle.dto.VehicleDto;
import inc.visor.voom_service.vehicle.model.Vehicle;
import inc.visor.voom_service.vehicle.model.VehicleType;
import inc.visor.voom_service.vehicle.service.VehicleService;
import inc.visor.voom_service.vehicle.service.VehicleTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final VehicleTypeService vehicleTypeService;
    private final DriverService driverService;

    public VehicleController(VehicleService vehicleService, VehicleTypeService vehicleTypeService, DriverService driverService) {
        this.vehicleService = vehicleService;
        this.vehicleTypeService = vehicleTypeService;
        this.driverService = driverService;
    }

    @GetMapping
    public ResponseEntity<List<VehicleDto>> getVehicles() {
        return ResponseEntity.ok(vehicleService.getVehicles().stream().map(VehicleDto::new).toList());
    }

    @GetMapping("{id}")
    public ResponseEntity<VehicleDto> getVehicle(@RequestParam long id) {
        Vehicle vehicle = vehicleService.getVehicle(id).orElseThrow(NotFoundException::new);
        return ResponseEntity.ok(new VehicleDto(vehicle));
    }

    @PostMapping
    public ResponseEntity<VehicleDto> createVehicle(@RequestBody CreateVehicleDto dto) {
        Driver driver = driverService.getDriver(dto.getDriverId()).orElseThrow(NotFoundException::new);
        VehicleType vehicleType = vehicleTypeService.getVehicleType(dto.getVehicleTypeId()).orElseThrow(NotFoundException::new);
        Vehicle vehicle = new Vehicle(dto, vehicleType, driver);
        vehicle = vehicleService.create(vehicle);
        return ResponseEntity.ok(new VehicleDto(vehicle));
    }

    @PutMapping("{id}")
    public ResponseEntity<VehicleDto> updateVehicle(@PathVariable long id, @RequestBody VehicleDto dto) {
        Vehicle vehicle = vehicleService.getVehicle(id).orElseThrow(NotFoundException::new);
        Driver driver = driverService.getDriver(dto.getDriverId()).orElseThrow(NotFoundException::new);
        VehicleType vehicleType = vehicleTypeService.getVehicleType(dto.getVehicleTypeId()).orElseThrow(NotFoundException::new);
        vehicle = new Vehicle(dto, vehicleType, driver);
        vehicle = vehicleService.update(vehicle);
        return ResponseEntity.ok(new VehicleDto(vehicle));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable long id) {
        Vehicle vehicle = vehicleService.getVehicle(id).orElseThrow(NotFoundException::new);
        vehicleService.delete(vehicle.getId());
        return ResponseEntity.noContent().build();
    }

}
