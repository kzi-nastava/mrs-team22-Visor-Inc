package inc.visor.voom_service.vehicle.controller;

import inc.visor.voom_service.vehicle.dto.CreateVehicleTypeDto;
import inc.visor.voom_service.vehicle.model.VehicleType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/vehicleTypes")
public class VehicleTypeController {

    VehicleTypeController() {}

    @GetMapping("")
    public ResponseEntity<List<VehicleType>> getVehicleTypes() {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleType> getVehicleType(@RequestParam long id) {
        return ResponseEntity.ok(new VehicleType());
    }

    @PostMapping("/{id}")
    public ResponseEntity<VehicleType> createVehicleType(@PathVariable long id, @RequestBody CreateVehicleTypeDto vehicleTypeDto) {
        return ResponseEntity.ok(new VehicleType());
    }

    @PostMapping("/{id}")
    public ResponseEntity<VehicleType> updateVehicleType(@PathVariable long id, @RequestBody VehicleType vehicleTypeDto) {
        return ResponseEntity.ok(vehicleTypeDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicleType(@PathVariable long id) {
        return ResponseEntity.ok().build();
    }

}
