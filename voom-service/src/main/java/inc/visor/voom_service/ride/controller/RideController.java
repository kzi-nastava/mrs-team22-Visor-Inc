package inc.visor.voom_service.ride.controller;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.model.VoomUserDetails;
import inc.visor.voom_service.auth.user.service.UserService;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.DriverStatus;
import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.exception.NotFoundException;
import inc.visor.voom_service.osrm.dto.LatLng;
import inc.visor.voom_service.osrm.service.RideWsService;
import inc.visor.voom_service.person.service.UserProfileService;
import inc.visor.voom_service.ride.dto.*;
import inc.visor.voom_service.ride.helpers.RideHistoryFormatter;
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.model.RideEstimationResult;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.RideRoute;
import inc.visor.voom_service.ride.model.enums.RideRequestStatus;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import inc.visor.voom_service.ride.model.enums.Sorting;
import inc.visor.voom_service.ride.service.*;
import inc.visor.voom_service.route.service.RideRouteService;
import inc.visor.voom_service.simulation.Simulator;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideRequestService rideRequestService;
    private final FavoriteRouteService favoriteRouteService;
    private final UserProfileService userProfileService;
    private final Simulator simulatorService;
    private final RideReportService rideReportService;
    private final RideService rideService;
    private final Simulator simulator;
    private final DriverService driverService;
    private final UserService userService;
    private final RideRouteService rideRouteService;
    private final RideEstimateService rideEstimateService;
    private final RideWsService rideWsService;

    public RideController(RideRequestService rideRequestService, FavoriteRouteService favoriteRouteService, RideReportService rideReportService, RideService rideService, UserProfileService userProfileService, Simulator simulatorService, Simulator simulator, DriverService driverService, UserService userService, RideRouteService rideRouteService, RideEstimateService rideEstimateService, RideWsService rideWsService) {
        this.rideRequestService = rideRequestService;
        this.favoriteRouteService = favoriteRouteService;
        this.rideReportService = rideReportService;
        this.rideService = rideService;
        this.userProfileService = userProfileService;
        this.simulatorService = simulatorService;
        this.simulator = simulator;
        this.driverService = driverService;
        this.userService = userService;
        this.rideRouteService = rideRouteService;
        this.rideEstimateService = rideEstimateService;
        this.rideWsService = rideWsService;
    }

    @PostMapping("/requests")
    public ResponseEntity<RideRequestResponseDto> createRideRequest(
            @Valid @RequestBody RideRequestCreateDto request,
            @AuthenticationPrincipal VoomUserDetails userDetails
    ) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        User user = userProfileService.getUserByEmail(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        RideRequestResponseDto response = rideRequestService.createRideRequest(request, user.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<RideResponseDto>> getRides(@RequestParam(required = false, defaultValue = "false") boolean ongoing) {
        List<Ride> rides = this.rideService.getRides();
        List<RideResponseDto> rideResponseDtos = rides.stream().map(RideResponseDto::new).toList();
        return ResponseEntity.ok(rideResponseDtos);
    }

    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<RideHistoryDto>> getRidesForUser(@PathVariable long userId, @RequestParam(required = false) LocalDateTime date) {
        List<Ride> ridesList = rideService.getUserRides(userId, null, null, Sorting.ASC);
        List<RideHistoryDto> rideHistoryDtoList = ridesList.stream().map(RideHistoryDto::new).toList();
        return ResponseEntity.ok(rideHistoryDtoList);
    }

    @GetMapping("/driver/{driverId}/history")
    public ResponseEntity<List<RideHistoryDto>> getRidesForDriver(@PathVariable long driverId, @RequestParam(required = false) LocalDateTime date) {
        List<Ride> ridesList = rideService.getDriverRides(driverId, null, null, Sorting.ASC);
        List<RideHistoryDto> rideHistoryDtoList = ridesList.stream().map(RideHistoryDto::new).toList();
        return ResponseEntity.ok(rideHistoryDtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideResponseDto> getRide(@PathVariable Long id) {
        Ride ride = this.rideService.getRide(id).orElseThrow(NotFoundException::new);
        return ResponseEntity.ok(new RideResponseDto(ride));
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
        final Driver driver = this.driverService.getDriverFromUser(id).orElseThrow(RuntimeException::new);
        driver.setStatus(DriverStatus.AVAILABLE);
        this.driverService.updateDriver(driver);
        final Ride ride = this.rideService.getRide(id).orElseThrow(NotFoundException::new);
        final RideRequest rideRequest = ride.getRideRequest();
        rideRequest.setCancelledBy(user);
        rideRequest.setReason(dto.getMessage());
        this.rideRequestService.update(rideRequest);
        ride.setStatus(RideStatus.CANCELLED);

        RideResponseDto rideResponse = new RideResponseDto(this.rideService.update(ride));
        this.rideWsService.sendRideChanges(rideResponse);
        return ResponseEntity.ok(rideResponse);
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<RideResponseDto> stopRide(@PathVariable Long id, @RequestBody RideStopDto dto) {
        final Driver driver = this.driverService.getDriverFromUser(dto.getUserId()).orElseThrow(RuntimeException::new);
        driver.setStatus(DriverStatus.AVAILABLE);
        driverService.save(driver);

        final Ride ride = this.rideService.getRide(id).orElseThrow(NotFoundException::new);
        final RideRequest rideRequest = ride.getRideRequest();
        final RideRoute rideRoute = rideRequest.getRideRoute();

        rideRoute.setTotalDistanceKm(this.rideEstimateService.calculateTotalDistance(dto.getPoints()));

        rideRequest.setRideRoute(this.rideRouteService.update(rideRoute));

        final RideEstimationResult rideEstimationResult = this.rideEstimateService.estimate(dto.getPoints(), ride.getRideRequest().getVehicleType());
        rideRequest.setCalculatedPrice(rideEstimationResult.price());

        ride.setFinishedAt(dto.getTimestamp());
        ride.setStatus(RideStatus.STOPPED);
        ride.setRideRequest(this.rideRequestService.update(rideRequest));

        RideResponseDto rideResponse = new RideResponseDto(this.rideService.update(ride));
        this.rideWsService.sendRideChanges(rideResponse);
        return ResponseEntity.ok(rideResponse);
    }

    @PostMapping("/{id}/panic")
    public ResponseEntity<RideResponseDto> panic(@PathVariable Long id, @RequestBody RidePanicDto dto) {
        final User user = this.userService.getUser(dto.getUserId()).orElseThrow(RuntimeException::new);
        final Ride ride = this.rideService.getRide(id).orElseThrow(NotFoundException::new);
        final RideRequest rideRequest = ride.getRideRequest();
        ride.setStatus(RideStatus.PANIC);
        final RideRequest updatedRideRequest = this.rideRequestService.update(rideRequest);
        ride.setRideRequest(updatedRideRequest);

        final Driver driver = this.driverService.getDriverFromUser(id).orElseThrow(RuntimeException::new);
        driver.setStatus(DriverStatus.AVAILABLE);
        this.driverService.updateDriver(driver);

        simulator.setFinishedRide(ride.getDriver().getId());

        RideResponseDto rideResponse = new RideResponseDto(this.rideService.update(ride));
        this.rideWsService.sendRidePanic(rideResponse);
        return ResponseEntity.ok(rideResponse);
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

        ActiveRideDto activeRide = rideService.getActiveRide(user);

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

        long userId = user.getId();

        Driver driver = driverService.getDriver(userId).orElseThrow(NotFoundException::new);
        driver.setStatus(DriverStatus.AVAILABLE);

        driverService.save(driver);

        ActiveRideDto activeRideDto = driverService.getActiveRide(userId);
        Ride ride = rideService.findById(activeRideDto.getRideId());

        rideService.finishRide(ride.getId());

        rideWsService.sendRideChanges(new RideResponseDto(ride));
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
            RideHistoryDto rideHistoryDto = RideHistoryFormatter.getRideHistoryDto(ride);
            rides.add(rideHistoryDto);
        }


        return ResponseEntity.ok(rides);
    }

    private static RideHistoryDto getRideHistoryDto(Ride ride) {
        return new RideHistoryDto(ride);
    }

    // for some reason doesnt work if isnt in this class, cant find solution rn
    private Driver extractDriver(VoomUserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        User user = userProfileService.getUserByEmail(username);
        System.out.println("User: " + user);
        if (user == null) {
            return null;
        }

        long userId = user.getId();

        return driverService.getDriver(userId).orElseThrow(NotFoundException::new);
    }

    @GetMapping("/user/{userId}/scheduled")
    public ResponseEntity<List<RideRequestResponseDto>> getScheduledRides(@PathVariable long userId) {
        final List<RideRequest> ongoingRideRequests = this.rideRequestService.getOngoingRideRequests();
        final List<RideRequest> filteredOngoingRideRequests = ongoingRideRequests.stream().filter(rideRequest -> rideRequest.getCreator().getId() == userId).toList();
        return ResponseEntity.ok(filteredOngoingRideRequests.stream().map(RideRequestResponseDto::new).toList());
    }

    @PostMapping("/scheduled/{id}/cancel")
    public ResponseEntity<RideRequestResponseDto> cancelScheduledRide(@PathVariable Long id, @RequestBody RideCancellationDto dto) {
        final User user = this.userService.getUser(dto.getUserId()).orElseThrow(RuntimeException::new);
        final RideRequest rideRequest = this.rideRequestService.getRideRequest(id).orElseThrow(NotFoundException::new);
        rideRequest.setCancelledBy(user);
        rideRequest.setStatus(RideRequestStatus.CANCELLED);
        return ResponseEntity.ok(new RideRequestResponseDto(this.rideRequestService.update(rideRequest)));
    }
}
