package inc.visor.voom_service.driver.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import inc.visor.voom_service.activation.service.ActivationTokenService;
import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.driver.dto.ActivateDriverRequestDto;
import inc.visor.voom_service.driver.dto.CreateDriverDto;
import inc.visor.voom_service.driver.dto.DriverSummaryDto;
import inc.visor.voom_service.driver.dto.ReportDriverRequestDto;
import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.ride.dto.RideResponseDto;
import inc.visor.voom_service.vehicle.dto.VehicleSummaryDto;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;
    private final ActivationTokenService activationTokenService;

    public DriverController(DriverService driverService, ActivationTokenService activationTokenService) {
        this.driverService = driverService;
        this.activationTokenService = activationTokenService;
    }

    @PostMapping
    public ResponseEntity<Void> createDriver(@Valid @RequestBody CreateDriverDto request) {

        driverService.createDriver(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // we would use message mapping in real world if driver location came from actual driver and not from simulation
//    @MessageMapping("/drivers.updateLocation")
//    @SendTo("/topic/driversLocations")
//    public DriverLocationDto updateLocation(@Payload DriverLocationDto request) {
//        return request;
//    }
    @GetMapping("/active")
    public ResponseEntity<List<DriverSummaryDto>> getActiveDrivers() {
        List<DriverSummaryDto> response = driverService.getActiveDrivers();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<VehicleSummaryDto> getMyDriverInfo(@AuthenticationPrincipal User user) {
        Long userId = (user != null) ? user.getId() : 2L;

        return ResponseEntity.ok(driverService.getVehicle(userId));
    }

    @PutMapping("/me")
    public ResponseEntity<VehicleSummaryDto> updateMyDriverInfo(@AuthenticationPrincipal User user, @RequestBody VehicleSummaryDto request) {
        Long userId = (user != null) ? user.getId() : 2L;

        return ResponseEntity.ok(driverService.updateVehicle(userId, request));
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

        activationTokenService.activateAccount(
                request.getToken(),
                request.getPassword(),
                request.getConfirmPassword()
        );

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{driverId}/status")
    public ResponseEntity<DriverSummaryDto> updateDriver(@PathVariable Long driverId, @RequestParam String status) {
        DriverSummaryDto response = new DriverSummaryDto();
        return ResponseEntity.ok(response);
    }

}
