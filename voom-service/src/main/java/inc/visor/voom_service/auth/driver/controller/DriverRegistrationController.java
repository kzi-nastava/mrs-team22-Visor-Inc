package inc.visor.voom_service.auth.driver.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import inc.visor.voom_service.auth.driver.dto.ActivateDriverRequestDto;
import inc.visor.voom_service.auth.driver.dto.CreateDriverRequestDto;
import inc.visor.voom_service.auth.driver.dto.CreateDriverResponseDto;
import inc.visor.voom_service.domain.ride.enums.DriverAccountStatus;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/drivers")
public class DriverRegistrationController {
    
    @PostMapping
    public ResponseEntity<CreateDriverResponseDto> registerDriver(
        @Valid @RequestBody CreateDriverRequestDto request
    ) {
        CreateDriverResponseDto response = new CreateDriverResponseDto(
            1L,
            request.getEmail(),
            DriverAccountStatus.PENDING_ACTIVATION
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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

}
