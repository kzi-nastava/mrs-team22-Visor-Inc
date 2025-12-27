package inc.visor.voom_service.driver.controller;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.driver.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import inc.visor.voom_service.ride.model.enums.DriverAccountStatus;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {
    
    @PostMapping
    public ResponseEntity<CreateDriverResponseDto> createDriver(@Valid @RequestBody CreateDriverRequestDto request) {
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

    @PostMapping("/{driverId}/reportDriver")
    public ResponseEntity<Void> reportDriver(@PathVariable Long driverId, @RequestBody ReportDriverDto req, @AuthenticationPrincipal User user) {
        // get needed info and pass into DriverService
    }





    //TODO remove, this is under user

//    @GetMapping("/activation")
//    public ResponseEntity<Boolean> checkActivationToken(
//        @RequestParam String token
//    ) {
//        return ResponseEntity.ok(true);
//    }
//
//    @PostMapping("/activation")
//    public ResponseEntity<String> activateDriver(
//        @Valid @RequestBody ActivateDriverRequestDto request
//    ) {
//        return ResponseEntity.ok("Driver account activated successfully.");
//    }

}
