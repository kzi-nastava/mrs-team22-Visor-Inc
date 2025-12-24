package inc.visor.voom_service.ride.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import inc.visor.voom_service.auth.driver.dto.DriverSummaryDto;
import inc.visor.voom_service.ride.dto.CreateRideRequestDto;
import inc.visor.voom_service.ride.dto.RideRequestResponseDto;
import inc.visor.voom_service.ride.dto.RideResponseDto;
import inc.visor.voom_service.ride.model.enums.RideRequestStatus;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rides")
public class RideController {
    
    public RideController() {};

    @PostMapping
    public ResponseEntity<RideRequestResponseDto> createRideRequest(
        @Valid @RequestBody CreateRideRequestDto request
    ) {

        DriverSummaryDto driver = new DriverSummaryDto(1L, "John", "Doe");
        RideRequestResponseDto response = new RideRequestResponseDto(
            1L,
            RideRequestStatus.ACCEPTED,
            15.0,
            request.getScheduledTime(),
            driver
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<RideResponseDto>> getRides(
        @RequestParam(required = false, defaultValue = "false") boolean ongoing
    ) {

        RideResponseDto ride = new RideResponseDto(
            1L,
            ongoing ? RideStatus.ONGOING : RideStatus.FINISHED,
            LocalDateTime.now().minusMinutes(10),
            ongoing ? null : LocalDateTime.now(),
            "John Doe",
            "Mark Smith"
        );

        return ResponseEntity.ok(List.of(ride));
    }




    // @PostMapping("/requests/favorites/{id}")
    // public ResponseEntity<RideRequestResponseDto> createRideRequestFromFavorite(
    //     @AuthenticationPrincipal User user,
    //     @PathVariable Long id,
    //     @RequestBody @Valid CreateRideFromFavoriteRouteDto request
    // ) {
    //     if (user == null) {
    //         return ResponseEntity.ok().body(null);
    //     }

    //     return ResponseEntity.ok(
    //         rideService.createFromFavorite(user, id, request));
    // }

    @PostMapping("/start/{id}")
    public ResponseEntity<String> startRide(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok("Ride started successfully.");
    }

}
