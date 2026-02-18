package inc.visor.voom_service.driver.controller;

import inc.visor.voom_service.driver.dto.DriverVehicleChangeRequestDto;
import inc.visor.voom_service.driver.service.DriverVehicleChangeRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/vehicle-requests")
@RequiredArgsConstructor
public class AdminVehicleRequestController {

    private final DriverVehicleChangeRequestService service;

    @GetMapping("/{id}")
    public ResponseEntity<DriverVehicleChangeRequestDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<DriverVehicleChangeRequestDto>> getPending() {
        return ResponseEntity.ok(service.getAllPending());
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable Long id) {
        service.approve(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable Long id) {
        service.reject(id);
        return ResponseEntity.ok().build();
    }
}
