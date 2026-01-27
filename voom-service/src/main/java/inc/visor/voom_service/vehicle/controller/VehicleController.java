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

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import inc.visor.voom_service.mail.EmailService;

@RestController
@RequestMapping("api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final VehicleTypeService vehicleTypeService;
    private final DriverService driverService;
    private final EmailService emailService;

    public VehicleController(VehicleService vehicleService, VehicleTypeService vehicleTypeService, DriverService driverService, EmailService emailService) {
        this.vehicleService = vehicleService;
        this.vehicleTypeService = vehicleTypeService;
        this.driverService = driverService;
        this.emailService = emailService;
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
    public ResponseEntity<VehicleDto> createVehicle(@Valid @RequestBody CreateVehicleDto dto) {
        Driver driver = driverService.getDriver(dto.getDriverId()).orElseThrow(NotFoundException::new);
        VehicleType vehicleType = vehicleTypeService.getVehicleType(dto.getVehicleTypeId()).orElseThrow(NotFoundException::new);
        Vehicle vehicle = new Vehicle(dto, vehicleType, driver);
        emailService.sendActivationEmail(driver.getUser());
        vehicle = vehicleService.create(vehicle);
        return ResponseEntity.ok(new VehicleDto(vehicle));
    }

    @PutMapping("{id}")
    public ResponseEntity<VehicleDto> updateVehicle(@PathVariable long id, @Valid @RequestBody VehicleDto dto) {
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
