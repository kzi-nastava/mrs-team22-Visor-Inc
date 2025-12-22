package inc.visor.voom_service.authorized.ride.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.authorized.ride.dto.CreateRideFromFavoriteRouteDto;
import inc.visor.voom_service.authorized.ride.dto.CreateRideRequestDto;
import inc.visor.voom_service.authorized.ride.dto.RideRequestResponseDto;
import inc.visor.voom_service.authorized.ride.service.RideService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rides")
public class RideController {
    
    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping("/requests")
    public ResponseEntity<RideRequestResponseDto> createRideRequest(
        @AuthenticationPrincipal User user,
        @Valid @RequestBody CreateRideRequestDto request
    ) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(null);
        }

        return ResponseEntity.ok(
                rideService.createRideRequest(user, request)
            );
    }


    @PostMapping("/requests/favorites/{id}")
    public ResponseEntity<RideRequestResponseDto> createRideRequestFromFavorite(
        @AuthenticationPrincipal User user,
        @PathVariable Long id,
        @RequestBody @Valid CreateRideFromFavoriteRouteDto request
    ) {
        if (user == null) {
            return ResponseEntity.ok().body(null);
        }

        return ResponseEntity.ok(
            rideService.createFromFavorite(user, id, request));
    }

    @PostMapping("/start/{id}")
    public ResponseEntity<String> startRide(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok("Ride started successfully.");
    }

}
