package inc.visor.voom_service.vehicle.controller;

import inc.visor.voom_service.vehicle.dto.CreateVehicleDto;
import inc.visor.voom_service.vehicle.model.Vehicle;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/vehicles")
public class VehicleController {

    VehicleController() {}

    @GetMapping("")
    public ResponseEntity<List<Vehicle>> getVehicles() {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicle(@RequestParam long id) {
        return ResponseEntity.ok(new Vehicle());
    }

    @PostMapping("/{id}")
    public ResponseEntity<Vehicle> createVehicle(@PathVariable long id, @RequestBody CreateVehicleDto VehicleDto) {
        return ResponseEntity.ok(new Vehicle());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable long id, @RequestBody Vehicle VehicleDto) {
        return ResponseEntity.ok(VehicleDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable long id) {
        return ResponseEntity.ok().build();
    }

}
