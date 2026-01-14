package inc.visor.voom_service.ride.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.ride.dto.RideCancelDto;
import inc.visor.voom_service.ride.dto.RideHistoryDto;
import inc.visor.voom_service.ride.dto.RideRequestCreateDTO;
import inc.visor.voom_service.ride.dto.RideRequestResponseDto;
import inc.visor.voom_service.ride.dto.RideResponseDto;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import inc.visor.voom_service.ride.service.RideRequestService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideRequestService rideRequestService;

    public RideController(RideRequestService rideRequestService) {
        this.rideRequestService = rideRequestService;
    }

    @PostMapping("/requests")
    public ResponseEntity<RideRequestResponseDto> createRideRequest(
            @Valid @RequestBody RideRequestCreateDTO request,
            @AuthenticationPrincipal User user
    ) {

        Long userId = (user != null) ? user.getId() : 1L;

        RideRequestResponseDto response
                = rideRequestService.createRideRequest(request, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<RideResponseDto>> getRides(@RequestParam(required = false, defaultValue = "false") boolean ongoing) {

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

    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<RideHistoryDto>> getRidesForUser(@PathVariable long userId, @RequestParam(required = false) LocalDateTime date) {

        RideHistoryDto ride = new RideHistoryDto();

        return ResponseEntity.ok(List.of(ride));
    }

    @GetMapping("/driver/{driverId}/history")
    public ResponseEntity<List<RideHistoryDto>> getRidesForDriver(@PathVariable long driverId, @RequestParam(required = false) LocalDateTime date) {

        RideHistoryDto ride = new RideHistoryDto();

        return ResponseEntity.ok(List.of(ride));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideResponseDto> getRide(@PathVariable Long id) {

        RideResponseDto ride = new RideResponseDto(
                1L,
                RideStatus.FINISHED,
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now(),
                "John Doe",
                "Mark Smith"
        );

        return ResponseEntity.ok(ride);
    }

    @PostMapping("/{id}")
    public ResponseEntity<RideRequestResponseDto> updateRide(@PathVariable Long id, @Valid @RequestBody RideRequestResponseDto request) {
        return ResponseEntity.ok(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRide(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
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
    @PostMapping("/{id}/cancel")
    public ResponseEntity<RideResponseDto> cancelRide(@PathVariable Long Id, @Valid @RequestBody RideCancelDto request) {

        RideResponseDto ride = new RideResponseDto(
                1L,
                RideStatus.FINISHED,
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now(),
                "John Doe",
                "Mark Smith"
        );

        return ResponseEntity.ok(ride);
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<String> startRide(@PathVariable Long id) {
        return ResponseEntity.ok("Ride started successfully.");
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<RideResponseDto> stopRide(@PathVariable Long id) {

        RideResponseDto ride = new RideResponseDto(
                1L,
                RideStatus.FINISHED,
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now(),
                "John Doe",
                "Mark Smith"
        );

        return ResponseEntity.ok(ride);
    }

}
