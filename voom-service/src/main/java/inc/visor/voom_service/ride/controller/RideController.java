package inc.visor.voom_service.ride.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import inc.visor.voom_service.auth.user.service.UserService;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.DriverStatus;
import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.exception.NotFoundException;
import inc.visor.voom_service.ride.dto.*;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.RideRoute;
import inc.visor.voom_service.ride.model.enums.Sorting;
import inc.visor.voom_service.ride.repository.RideRepository;
import inc.visor.voom_service.ride.service.*;
import org.springframework.format.annotation.DateTimeFormat;
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
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import inc.visor.voom_service.simulation.Simulator;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideRequestService rideRequestService;
    private final FavoriteRouteService favoriteRouteService;
    private final UserProfileService userProfileService;
    private final Simulator simulatorService;
    private final RideReportService rideReportService;
    private final RideService rideService;
    private final RideRepository rideRepository;
    private final Simulator simulator;
    private final DriverService driverService;
    private final UserService userService;
    private final RideRouteService rideRouteService;

    public RideController(RideRequestService rideRequestService, FavoriteRouteService favoriteRouteService, RideReportService rideReportService, RideService rideService, UserProfileService userProfileService, Simulator simulatorService, RideRepository rideRepository, Simulator simulator, DriverService driverService, UserService userService, RideRouteService rideRouteService) {
        this.rideRequestService = rideRequestService;
        this.favoriteRouteService = favoriteRouteService;
        this.rideReportService = rideReportService;
        this.rideService = rideService;
        this.userProfileService = userProfileService;
        this.simulatorService = simulatorService;
        this.rideRepository = rideRepository;
        this.simulator = simulator;
        this.driverService = driverService;
        this.userService = userService;
        this.rideRouteService = rideRouteService;
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

        List<RideHistoryDto> rides = new ArrayList<>();

        List<Ride> ridesList = rideService.getDriverRides(driverId, null, null, Sorting.ASC);


        for (Ride ride : ridesList) {
            RideHistoryDto rideHistoryDto = getRideHistoryDto(ride);
            rides.add(rideHistoryDto);
        }


        return ResponseEntity.ok(rides);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideResponseDto> getRide(@PathVariable Long id) {

//        RideResponseDto ride = new RideResponseDto(
//                1L,
//                RideStatus.ONGOING,
//                LocalDateTime.of(2026, 1, 15, 14, 30),
//                null,
//                "Marko Marković",
//                "Petar Petrović",
//                1L,
//                "Bulevar Oslobođenja 45, Novi Sad",
//                "Zmaj Jovina 12, Novi Sad"
//        );

        RideResponseDto rideDto = new RideResponseDto();

        rideRepository.findById(id).ifPresent(ride -> rideDto.setId(ride.getId()));



        return ResponseEntity.ok(rideDto);
    }

    @PostMapping("/scheduled/{id}")
    public ResponseEntity<Void> scheduleRide(@PathVariable Long id, @Valid @RequestBody StartScheduleRideDto request) {

        rideService.startScheduleRide(id);

        simulatorService.changeDriverRoute(request.getDriverId(), request.getLat(), request.getLng());

        return ResponseEntity.ok().build();
    }

    //FIXME @nikola0231 move to FavoriteRouteController

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

    //FIXME @nikola0231 move to FavoriteRouteController

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

    //FIXME @nikola0231 move to FavoriteRouteController

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

    @PostMapping("/{id}/cancel")
    public ResponseEntity<RideResponseDto> cancelRide(@PathVariable Long id, @RequestBody RideCancellationDto dto) {
        final User user = this.userService.getUser(dto.getUserId()).orElseThrow(RuntimeException::new);
        final Ride ride = this.rideService.getRide(id).orElseThrow(NotFoundException::new);
        final RideRequest rideRequest = ride.getRideRequest();
        rideRequest.setCancelledBy(user);
        rideRequest.setReason(dto.getMessage());
        this.rideRequestService.update(rideRequest);
        ride.setStatus(RideStatus.CANCELLED);
        return ResponseEntity.ok(new RideResponseDto(this.rideService.update(ride)));
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<RideResponseDto> stopRide(@PathVariable Long id, @RequestBody RideStopDto dto) {
        final Driver driver = this.driverService.getDriver(dto.getDriverId()).orElseThrow(RuntimeException::new);
        final Ride ride = this.rideService.getRide(id).orElseThrow(NotFoundException::new);
        ride.setFinishedAt(dto.getTimestamp());
        ride.setStatus(RideStatus.STOPPED);
        final RideRequest rideRequest = ride.getRideRequest();
        final RideRoute rideRoute = rideRequest.getRideRoute();

        //TODO implement dropoff and price change
        this.rideRequestService.update(rideRequest);
        return ResponseEntity.ok(new RideResponseDto(this.rideService.update(ride)));
    }

    @PostMapping("/{id}/panic")
    public ResponseEntity<RideResponseDto> panic(@PathVariable Long id, @RequestBody RidePanicDto dto) {
        final Ride ride = this.rideService.getRide(id).orElseThrow(NotFoundException::new);
        final RideRequest rideRequest = ride.getRideRequest();
        this.rideRequestService.update(rideRequest);
        //TODO publish change to websockets
        ride.setStatus(RideStatus.PANIC);
        return ResponseEntity.ok(new RideResponseDto(this.rideService.update(ride)));
    }

    @PostMapping("/{id}/report")
    public ResponseEntity<RideResponseDto> reportRide(@PathVariable Long id, @RequestBody RideReportRequestDto body) {
        rideReportService.reportRide(id, body.getMessage());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ongoing")
    public ResponseEntity<ActiveRideDto> getMethodName(@AuthenticationPrincipal VoomUserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        User user = userProfileService.getUserByEmail(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ActiveRideDto activeRide = rideService.getActiveRide(user.getId());

        return ResponseEntity.ok(activeRide);
    }

    @PostMapping("/finish-ongoing")
    public ResponseEntity<ActiveRideDto> finishRide(@AuthenticationPrincipal VoomUserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        User user = userProfileService.getUserByEmail(username);
        System.out.println("User: " + user);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = user.getId();

        Driver driver = driverService.getDriver(userId).get();
        driver.setStatus(DriverStatus.AVAILABLE);

        driverService.save(driver);

        ActiveRideDto activeRideDto = driverService.getActiveRide(userId);
        Ride ride = rideService.findById(activeRideDto.getRideId());
        ride.setFinishedAt(LocalDateTime.now());
        ride.setStatus(RideStatus.FINISHED);
        rideService.save(ride);

        simulator.setFinishedRide(userId);

        return ResponseEntity.ok(activeRideDto);
    }

    @GetMapping("/driver/history")
    public ResponseEntity<List<RideHistoryDto>> getRidesForDriver(
            @AuthenticationPrincipal VoomUserDetails userDetails,
            @RequestParam(name = "dateFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(name="dateTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(name="sort", required = true) Sorting sort
            ) {

        List<RideHistoryDto> rides = new ArrayList<>();

        Driver driver = extractDriver(userDetails);

        System.out.println("AAA Driver: " + driver);

        if (driver == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        System.out.println("date from: " + dateFrom);
        System.out.println("date to: " + dateTo);

//        if (dateTo != null) {
//            dateTo.plusHours(23);
//            dateTo.plusMinutes(59);
//        }

        List<Ride> ridesList = rideService.getDriverRides(driver.getId(), dateFrom, dateTo, sort);

        for (Ride ride : ridesList) {
            RideHistoryDto rideHistoryDto = getRideHistoryDto(ride);
            rides.add(rideHistoryDto);
        }


        return ResponseEntity.ok(rides);
    }

    private static RideHistoryDto getRideHistoryDto(Ride ride) {
        RideHistoryDto rideHistoryDto = new RideHistoryDto();
        rideHistoryDto.setId(ride.getId());
        rideHistoryDto.setRideRequest(ride.getRideRequest());
        rideHistoryDto.setRideRoute(ride.getRideRequest().getRideRoute());
        rideHistoryDto.setCancelledBy(ride.getRideRequest().getCancelledBy());
        rideHistoryDto.setPassengers(ride.getPassengers());
        rideHistoryDto.setStatus(ride.getStatus());
        rideHistoryDto.setFinishedAt(ride.getFinishedAt());
        rideHistoryDto.setStartedAt(ride.getStartedAt());
        return rideHistoryDto;
    }

    private Driver extractDriver(VoomUserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        User user = userProfileService.getUserByEmail(username);
        System.out.println("User: " + user);
        if (user == null) {
            return null;
        }

        Long userId = user.getId();

        return driverService.getDriver(userId).get();
    }

}
