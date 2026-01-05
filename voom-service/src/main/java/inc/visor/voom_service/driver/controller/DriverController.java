package inc.visor.voom_service.driver.controller;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.driver.dto.*;
import inc.visor.voom_service.ride.dto.RideResponseDto;
import inc.visor.voom_service.ride.model.enums.DriverAccountStatus;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.vehicle.dto.VehicleSummaryDto;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    public ResponseEntity<CreateDriverResponseDto> createDriver(@Valid @RequestBody CreateDriverDto request) {
        CreateDriverResponseDto response = new CreateDriverResponseDto(
                1L,
                request.getEmail(),
                DriverAccountStatus.PENDING_ACTIVATION
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // we would use message mapping in real world if driver location came from actual driver and not from simulation
//    @MessageMapping("/drivers.updateLocation")
//    @SendTo("/topic/driversLocations")
//    public DriverLocationDto updateLocation(@Payload DriverLocationDto request) {
//        return request;
//    }
    @GetMapping("/active")
    public ResponseEntity<List<DriverSummaryDto>> getActiveDrivers() {
        List<DriverSummaryDto> response = new ArrayList<>();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<VehicleSummaryDto> getMyDriverInfo(@AuthenticationPrincipal User user) {
        Long userId = (user != null) ? user.getId() : 2L;

        return ResponseEntity.ok(driverService.getVehicle(userId));
    }

    @PostMapping("/{driverId}/report")
    public ResponseEntity<Void> reportDriver(@PathVariable Long driverId, @RequestBody ReportDriverRequestDto req, @AuthenticationPrincipal User user) {
        // get needed info and pass into DriverService
        // driverService.reportDriver(driverId, req.getComment(), user.getId())
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{driverId}/history")
    public ResponseEntity<List<RideResponseDto>> getRideHistory(@PathVariable Long driverId, @AuthenticationPrincipal User user) {
        List<RideResponseDto> response = new ArrayList<>();

        // if (user is not driver) -> obliterate him { return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/activation")
    public ResponseEntity<Boolean> checkActivationToken(
            @RequestParam String token
    ) {
        return ResponseEntity.ok(true);
    }

    @PostMapping("/activation")
    public ResponseEntity<String> activateDriver(
            @Valid @RequestBody ActivateDriverRequestDto request
    ) {
        return ResponseEntity.ok("Driver account activated successfully.");
    }

    @PutMapping("/{driverId}/status")
    public ResponseEntity<DriverSummaryDto> updateDriver(@PathVariable Long driverId, @RequestParam String status) {
        DriverSummaryDto response = new DriverSummaryDto();
        return ResponseEntity.ok(response);
    }

}
