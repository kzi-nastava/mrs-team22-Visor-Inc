package inc.visor.voom_service.vehicle.controller;

import inc.visor.voom_service.exception.NotFoundException;
import inc.visor.voom_service.vehicle.dto.CreateVehicleTypeDto;
import inc.visor.voom_service.vehicle.dto.VehicleTypeDto;
import inc.visor.voom_service.vehicle.model.VehicleType;
import inc.visor.voom_service.vehicle.service.VehicleTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/vehicleTypes")
public class VehicleTypeController {

    private final VehicleTypeService vehicleTypeService;

    public VehicleTypeController(VehicleTypeService vehicleTypeService) {
        this.vehicleTypeService = vehicleTypeService;
    }

    @GetMapping
    public ResponseEntity<List<VehicleTypeDto>> getVehicleTypes() {
        return ResponseEntity.ok(vehicleTypeService.getVehicleTypes().stream().map(VehicleTypeDto::new).toList());
    }

    @GetMapping("{id}")
    public ResponseEntity<VehicleTypeDto> getVehicleType(@RequestParam long id) {
        VehicleType vehicleType = vehicleTypeService.getVehicleType(id).orElseThrow(NotFoundException::new);
        return ResponseEntity.ok(new VehicleTypeDto(vehicleType));
    }

    @PostMapping
    public ResponseEntity<VehicleTypeDto> createVehicleType(@RequestBody CreateVehicleTypeDto dto) {
        VehicleType vehicleType = new VehicleType(dto);
        vehicleType = vehicleTypeService.create(vehicleType);
        return ResponseEntity.ok(new VehicleTypeDto(vehicleType));
    }

    @PutMapping("{id}")
    public ResponseEntity<VehicleTypeDto> updateVehicleType(@PathVariable long id, @RequestBody VehicleTypeDto dto) {
        VehicleType vehicleType = vehicleTypeService.getVehicleType(id).orElseThrow(NotFoundException::new);
        vehicleType.setPrice(dto.getPrice());
        vehicleType = vehicleTypeService.update(vehicleType);
        return ResponseEntity.ok(new VehicleTypeDto(vehicleType));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteVehicleType(@PathVariable long id) {
        VehicleType vehicleType = vehicleTypeService.getVehicleType(id).orElseThrow(NotFoundException::new);
        vehicleTypeService.delete(vehicleType.getId());
        return ResponseEntity.noContent().build();
    }

}
