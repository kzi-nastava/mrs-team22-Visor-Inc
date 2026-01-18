package inc.visor.voom_service.ride.controller;

import java.time.LocalDateTime;
import java.util.List;

import inc.visor.voom_service.rating.dto.RatingRequestDto;
import inc.visor.voom_service.rating.service.RatingService;
import inc.visor.voom_service.ride.dto.*;
import inc.visor.voom_service.ride.repository.RideRepository;
import inc.visor.voom_service.ride.service.RideReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
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
import inc.visor.voom_service.ride.model.enums.RideStatus;
import inc.visor.voom_service.ride.service.FavoriteRouteService;
import inc.visor.voom_service.ride.service.RideRequestService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideRequestService rideRequestService;
    private final FavoriteRouteService favoriteRouteService;
    private final RideReportService rideReportService;
    private final RatingService ratingService;

    public RideController(RideRequestService rideRequestService, FavoriteRouteService favoriteRouteService, RideReportService rideReportService, RatingService ratingService) {
        this.rideRequestService = rideRequestService;
        this.favoriteRouteService = favoriteRouteService;
        this.rideReportService = rideReportService;
        this.ratingService = ratingService;
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
                RideStatus.ONGOING,
                LocalDateTime.of(2026, 1, 15, 14, 30),
                null,
                "Marko Marković",
                "Petar Petrović",
                1L,
                "Bulevar Oslobođenja 45, Novi Sad",
                "Zmaj Jovina 12, Novi Sad"
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

    @PostMapping("/favorites")
    public ResponseEntity<Void> createFavoriteRoute(@AuthenticationPrincipal User user, @Valid @RequestBody CreateFavoriteRouteRequest request) {

        if (user == null) {
            long mockUserId = 2L;

            favoriteRouteService.create(mockUserId, request);

            return ResponseEntity.status(HttpStatus.CREATED).build();
            // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        favoriteRouteService.create(user.getId(), request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<FavoriteRouteDto>> getFavoriteRoutes(@AuthenticationPrincipal User user) {
        if (user == null) {
            long mockUserId = 2L;

            List<FavoriteRouteDto> favoriteRoutes = favoriteRouteService.getAllByUserId(mockUserId);

            return ResponseEntity.ok(favoriteRoutes);
            // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<FavoriteRouteDto> favoriteRoutes = favoriteRouteService.getAllByUserId(user.getId());
        return ResponseEntity.ok(favoriteRoutes);
    }
    
    @DeleteMapping("/favorites/{favoriteRouteId}")
    public ResponseEntity<Void> deleteFavoriteRoute(
            @AuthenticationPrincipal User user,
            @PathVariable Long favoriteRouteId
    ) {
        if (user == null) {
            long mockUserId = 2L;

            favoriteRouteService.delete(mockUserId, favoriteRouteId);

            return ResponseEntity.noContent().build();
            // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        favoriteRouteService.delete(user.getId(), favoriteRouteId);

        return ResponseEntity.noContent().build();
    }
    
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

    @PostMapping("/{id}/report")
    public ResponseEntity<RideResponseDto> reportRide(@PathVariable Long id, @RequestBody RideReportRequestDto body) {
        rideReportService.reportRide(id, body.getMessage());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{rideId}/rate")
    public ResponseEntity<Void> rateRide(
            @PathVariable Long rideId,
            @RequestBody RatingRequestDto request
    ) {
        ratingService.rateRide(rideId, request);
        System.out.println(rideId);
        return ResponseEntity.noContent().build();
    }

}
