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
import inc.visor.voom_service.auth.user.model.VoomUserDetails;
import inc.visor.voom_service.osrm.dto.LatLng;
import inc.visor.voom_service.person.service.UserProfileService;
import inc.visor.voom_service.ride.dto.CreateFavoriteRouteRequest;
import inc.visor.voom_service.ride.dto.FavoriteRouteDto;
import inc.visor.voom_service.ride.dto.RideCancelDto;
import inc.visor.voom_service.ride.dto.RideHistoryDto;
import inc.visor.voom_service.ride.dto.RideRequestCreateDTO;
import inc.visor.voom_service.ride.dto.RideRequestResponseDto;
import inc.visor.voom_service.ride.dto.RideResponseDto;
import inc.visor.voom_service.ride.dto.StartRideDto;
import inc.visor.voom_service.ride.dto.StartScheduleRideDto;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import inc.visor.voom_service.ride.service.FavoriteRouteService;
import inc.visor.voom_service.ride.service.RideRequestService;
import inc.visor.voom_service.ride.service.RideService;
import inc.visor.voom_service.simulation.Simulator;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideRequestService rideRequestService;
    private final FavoriteRouteService favoriteRouteService;
    private final UserProfileService userProfileService;
    private final RideService rideService;
    private final Simulator simulatorService;

    public RideController(RideRequestService rideRequestService, FavoriteRouteService favoriteRouteService, UserProfileService userProfileService, RideService rideService, Simulator simulatorService) {
        this.rideRequestService = rideRequestService;
        this.favoriteRouteService = favoriteRouteService;
        this.userProfileService = userProfileService;
        this.rideService = rideService;
        this.simulatorService = simulatorService;
    }

    @PostMapping("/requests")
    public ResponseEntity<RideRequestResponseDto> createRideRequest(
            @Valid @RequestBody RideRequestCreateDTO request,
            @AuthenticationPrincipal VoomUserDetails userDetails
    ) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        User user = userProfileService.getUserByEmail(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        RideRequestResponseDto response
                = rideRequestService.createRideRequest(request, user.getId());

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

    @PostMapping("/scheduled/{id}")
    public ResponseEntity<Void> scheduleRide(@PathVariable Long id, @Valid @RequestBody StartScheduleRideDto request) {

        rideService.startScheduleRide(id);

        simulatorService.changeDriverRoute(request.getDriverId(), request.getLat(), request.getLng());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/favorites")
    public ResponseEntity<Void> createFavoriteRoute(@AuthenticationPrincipal VoomUserDetails userDetails, @Valid @RequestBody CreateFavoriteRouteRequest request) {

        String username = userDetails != null ? userDetails.getUsername() : null;
        User user = userProfileService.getUserByEmail(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        favoriteRouteService.create(user.getId(), request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<FavoriteRouteDto>> getFavoriteRoutes(@AuthenticationPrincipal VoomUserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        User user = userProfileService.getUserByEmail(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<FavoriteRouteDto> favoriteRoutes = favoriteRouteService.getAllByUserId(user.getId());
        return ResponseEntity.ok(favoriteRoutes);
    }

    @DeleteMapping("/favorites/{favoriteRouteId}")
    public ResponseEntity<Void> deleteFavoriteRoute(
            @AuthenticationPrincipal VoomUserDetails userDetails,
            @PathVariable Long favoriteRouteId
    ) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        User user = userProfileService.getUserByEmail(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
    public ResponseEntity<String> startRide(@PathVariable Long id, @AuthenticationPrincipal VoomUserDetails userDetails, @RequestBody StartRideDto request) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        User user = userProfileService.getUserByEmail(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        rideService.startRide(id, user.getId(), request.getRoutePoints());

        List<LatLng> latLngPoints = request.getRoutePoints().stream()
                .map(point -> new LatLng(point.getLat(), point.getLng()))
                .toList();

        simulatorService.changeDriverRouteMultiplePoints(user.getId(), latLngPoints);

        return ResponseEntity.ok().build();
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
