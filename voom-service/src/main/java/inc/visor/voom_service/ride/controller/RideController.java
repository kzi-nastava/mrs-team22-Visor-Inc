package inc.visor.voom_service.ride.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
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
import inc.visor.voom_service.auth.user.service.UserService;
import inc.visor.voom_service.complaints.service.ComplaintService;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.DriverStatus;
import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.exception.NotFoundException;
import inc.visor.voom_service.osrm.dto.LatLng;
import inc.visor.voom_service.osrm.service.RideWsService;
import inc.visor.voom_service.person.service.UserProfileService;
import inc.visor.voom_service.ride.dto.ActiveRideDto;
import inc.visor.voom_service.ride.dto.CreateFavoriteRouteRequest;
import inc.visor.voom_service.ride.dto.FavoriteRouteDto;
import inc.visor.voom_service.ride.dto.RideCancellationDto;
import inc.visor.voom_service.ride.dto.RideHistoryDto;
import inc.visor.voom_service.ride.dto.RidePanicDto;
import inc.visor.voom_service.ride.dto.RideRequestCreateDto;
import inc.visor.voom_service.ride.dto.RideRequestResponseDto;
import inc.visor.voom_service.ride.dto.RideResponseDto;
import inc.visor.voom_service.ride.dto.RideStopDto;
import inc.visor.voom_service.ride.dto.StartRideDto;
import inc.visor.voom_service.ride.dto.StartScheduleRideDto;
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.model.RideEstimationResult;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.RideRoute;
import inc.visor.voom_service.ride.model.RoutePoint;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import inc.visor.voom_service.ride.model.enums.Sorting;
import inc.visor.voom_service.ride.service.FavoriteRouteService;
import inc.visor.voom_service.ride.service.RideEstimateService;
import inc.visor.voom_service.ride.service.RideRequestService;
import inc.visor.voom_service.ride.service.RideService;
import inc.visor.voom_service.route.service.RideRouteService;
import inc.visor.voom_service.simulation.Simulator;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideRequestService rideRequestService;
    private final FavoriteRouteService favoriteRouteService;
    private final UserProfileService userProfileService;
    private final Simulator simulatorService;
    private final ComplaintService complaintService;
    private final RideService rideService;
    private final Simulator simulator;
    private final DriverService driverService;
    private final UserService userService;
    private final RideRouteService rideRouteService;
    private final RideEstimateService rideEstimateService;
    private final RideWsService rideWsService;

    public RideController(RideRequestService rideRequestService, FavoriteRouteService favoriteRouteService, ComplaintService complaintService, RideService rideService, UserProfileService userProfileService, Simulator simulatorService, Simulator simulator, DriverService driverService, UserService userService, RideRouteService rideRouteService, RideEstimateService rideEstimateService, RideWsService rideWsService) {
        this.rideRequestService = rideRequestService;
        this.favoriteRouteService = favoriteRouteService;
        this.complaintService = complaintService;
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
    public ResponseEntity<List<RideHistoryDto>> getRides(@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") @RequestParam(required = false) LocalDateTime start, @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") @RequestParam(required = false) LocalDateTime end, @RequestParam(defaultValue = "DESC") Sorting sort) {
        final List<Ride> rides;
        log.info("Start " + start + " " + "End " + end);
        List<Ride> allRides = this.rideService.getRides();
        rides = this.rideService.getRidesFilteredSortedByDate(start, end, sort, allRides);

        final List<RideHistoryDto> rideResponses = rides.stream().map(RideHistoryDto::new).toList();
        return ResponseEntity.ok(rideResponses);
    }

    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<RideHistoryDto>> getRidesForUser(@PathVariable long userId, @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") @RequestParam(required = false) LocalDateTime start, @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") @RequestParam(required = false) LocalDateTime end,  @RequestParam(defaultValue = "DESC") Sorting sort) {
        final List<Ride> ridesList = rideService.getUserRides(userId, start, end, sort);
        log.info("Start " + start + " " + "End " + end);
        final List<RideHistoryDto> rideHistoryDtoList = ridesList.stream().map(RideHistoryDto::new).toList();
        return ResponseEntity.ok(rideHistoryDtoList);
    }

    @GetMapping("/driver/{driverId}/history")
    public ResponseEntity<List<RideHistoryDto>> getRidesForDriver(@PathVariable long driverId, @RequestParam(required = false) LocalDateTime date) {
        final List<Ride> ridesList = rideService.getDriverRides(driverId, null, null, Sorting.ASC);
        final List<RideHistoryDto> rideHistoryDtoList = ridesList.stream().map(RideHistoryDto::new).toList();
        return ResponseEntity.ok(rideHistoryDtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideResponseDto> getRide(@PathVariable Long id) {
        final Ride ride = this.rideService.getRide(id).orElseThrow(NotFoundException::new);
        return ResponseEntity.ok(new RideResponseDto(ride));
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

    @PostMapping("/{id}/start")
    public ResponseEntity<String> startRide(@PathVariable Long id, @AuthenticationPrincipal VoomUserDetails userDetails, @Validated @RequestBody StartRideDto request) {
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
    public ResponseEntity<RideResponseDto> cancelRide(@PathVariable Long id, @Valid @RequestBody RideCancellationDto dto) {
        final User user = this.userService.getUser(dto.getUserId()).orElseThrow(RuntimeException::new);
        final Driver driver = this.driverService.getDriverFromUser(id).orElseThrow(RuntimeException::new);
        driver.setStatus(DriverStatus.AVAILABLE);
        this.driverService.updateDriver(driver);
        final Ride ride = this.rideService.getRide(id).orElseThrow(NotFoundException::new);
        final RideRequest rideRequest = ride.getRideRequest();
        rideRequest.setCancelledBy(user);
        rideRequest.setReason(dto.getMessage());
        this.rideRequestService.update(rideRequest);
        ride.setStatus(RideStatus.DRIVER_CANCELLED);

        RideResponseDto rideResponse = new RideResponseDto(this.rideService.update(ride));
        this.rideWsService.sendRideChanges(rideResponse);
        return ResponseEntity.ok(rideResponse);
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<RideResponseDto> stopRide(@PathVariable Long id, @Valid @RequestBody RideStopDto dto) {
        final Driver driver = this.driverService.getDriverFromUser(dto.getUserId()).orElseThrow(RuntimeException::new);
        driver.setStatus(DriverStatus.AVAILABLE);
        driverService.save(driver);

        final Ride ride = this.rideService.getRide(id).orElseThrow(NotFoundException::new);
        final RideRequest rideRequest = ride.getRideRequest();
        final RideRoute rideRoute = rideRequest.getRideRoute();
        final LatLng point = dto.getPoint();

        final List<RoutePoint> routePoints = rideRoute.getRoutePoints();

        final RoutePoint matched = routePoints.stream()
                .filter(rp ->
                        Double.compare(Math.round(rp.getLatitude()), Math.round(point.lat())) == 0 &&
                        Double.compare(Math.round(rp.getLongitude()), Math.round(point.lng())) == 0
                )
                .findFirst()
                .orElseThrow(NotFoundException::new);

        int orderIndex = matched.getOrderIndex();

        final List<RoutePoint> filteredRoutePoints = new ArrayList<>(routePoints.stream()
                .filter(rp -> rp.getOrderIndex() <= orderIndex)
                .sorted(Comparator.comparingInt(RoutePoint::getOrderIndex))
                .toList());

        filteredRoutePoints.add(new RoutePoint(dto.getPoint()));

        final List<RideRequestCreateDto.RoutePointDto> dtos = filteredRoutePoints.stream().map(RideRequestCreateDto.RoutePointDto::new).toList();

//        rideRoute.setRoutePoints(filteredRoutePoints);
        rideRoute.setTotalDistanceKm(this.rideEstimateService.calculateTotalDistance(dtos));
        rideRequest.setRideRoute(this.rideRouteService.update(rideRoute));

        final RideEstimationResult rideEstimationResult = this.rideEstimateService.estimate(dtos, ride.getRideRequest().getVehicleType());
        rideRequest.setCalculatedPrice(rideEstimationResult.price());

        ride.setFinishedAt(dto.getTimestamp());
        ride.setStatus(RideStatus.STOPPED);
        ride.setRideRequest(this.rideRequestService.update(rideRequest));

        simulator.setFinishedRide(ride.getDriver().getId());

        RideResponseDto rideResponse = new RideResponseDto(this.rideService.update(ride));
        this.rideWsService.sendRideChanges(rideResponse);
        return ResponseEntity.ok(rideResponse);
    }

    @PostMapping("/{id}/panic")
    public ResponseEntity<RideResponseDto> panic(@PathVariable Long id, @Valid @RequestBody RidePanicDto dto) {
        final User user = this.userService.getUser(dto.getUserId()).orElseThrow(RuntimeException::new);
        final Ride ride = this.rideService.getRide(id).orElseThrow(NotFoundException::new);
        final RideRequest rideRequest = ride.getRideRequest();
        ride.setStatus(RideStatus.PANIC);
        final RideRequest updatedRideRequest = this.rideRequestService.update(rideRequest);
        ride.setRideRequest(updatedRideRequest);
        ride.setFinishedAt(LocalDateTime.now());

        final Driver driver = this.driverService.getDriver(ride.getDriver().getId()).orElseThrow(RuntimeException::new);
        driver.setStatus(DriverStatus.AVAILABLE);
        this.driverService.updateDriver(driver);

        simulator.setFinishedRide(ride.getDriver().getId());

        RideResponseDto rideResponse = new RideResponseDto(this.rideService.update(ride));
        this.rideWsService.sendRidePanic(rideResponse);
        return ResponseEntity.ok(rideResponse);
    }

//    @PostMapping("/{id}/report")
//    public ResponseEntity<RideResponseDto> reportRide(@AuthenticationPrincipal VoomUserDetails userDetails, @PathVariable Long id, @RequestBody @Valid ComplaintRequestDto body) {
//        String username = userDetails != null ? userDetails.getUsername() : null;
//        User user = userProfileService.getUserByEmail(username);
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//        complaintService.reportRide(id, user, body.getMessage());
//        return ResponseEntity.noContent().build();
//    }

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

    @GetMapping("/ongoing/driver/{driverId}")
    public ResponseEntity<ActiveRideDto> getOngoingByDriverId(@PathVariable Long driverId, @AuthenticationPrincipal VoomUserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        User admin = userProfileService.getUserByEmail(username);

        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!admin.getUserRole().getRoleName().equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Driver> driver = driverService.getDriver(driverId);

        if (!driver.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User driverUser = userProfileService.getUserByEmail(driver.get().getUser().getEmail());

        ActiveRideDto activeRide = rideService.getActiveRide(driverUser);

        return activeRide != null ? ResponseEntity.ok(activeRide) : ResponseEntity.noContent().build();
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


        List<Ride> ridesList = rideService.getDriverRides(driver.getId(), dateFrom, dateTo, sort);

        for (Ride ride : ridesList) {
            rides.add(new RideHistoryDto(ride));
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
    public ResponseEntity<List<RideHistoryDto>> getScheduledRides( @PathVariable long userId) {
        final List<Ride> cancelledScheduledRides = this.rideService.getScheduledRides(RideStatus.USER_CANCELLED);
        final List<Ride> scheduledRides = this.rideService.getScheduledRides(RideStatus.SCHEDULED);
        scheduledRides.addAll(cancelledScheduledRides);
        final List<Ride> filteredScheduledRides = scheduledRides.stream().filter(scheduledRide -> scheduledRide.getRideRequest().getCreator().getId() == userId && !scheduledRide.getRideRequest().getScheduledTime().atZone(ZoneId.of("Europe/Belgrade")).isBefore(LocalDateTime.now().atZone(ZoneId.of("Europe/Belgrade"))) && !scheduledRide.getRideRequest().getScheduledTime().atZone(ZoneId.of("Europe/Belgrade")).isAfter(LocalDateTime.now().plusHours(5).atZone(ZoneId.of("Europe/Belgrade")))).toList();
        return ResponseEntity.ok(filteredScheduledRides.stream().map(RideHistoryDto::new).toList());
    }

    @GetMapping("/driver/scheduled")
    public ResponseEntity<List<RideHistoryDto>> getScheduledRidesDriver(@AuthenticationPrincipal VoomUserDetails userDetails) {
        final List<Ride> scheduledRides = this.rideService.getScheduledRides(RideStatus.SCHEDULED);
        try {
            final Driver driver = extractDriver(userDetails);
            if (driver == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            final List<Ride> filteredScheduledRides = scheduledRides.stream().filter(scheduledRide -> scheduledRide.getDriver().getId() == driver.getId() && !scheduledRide.getRideRequest().getScheduledTime().atZone(ZoneId.of("Europe/Belgrade")).isBefore(LocalDateTime.now().atZone(ZoneId.of("Europe/Belgrade"))) && !scheduledRide.getRideRequest().getScheduledTime().atZone(ZoneId.of("Europe/Belgrade")).isAfter(LocalDateTime.now().plusHours(5).atZone(ZoneId.of("Europe/Belgrade")))).toList();
            return ResponseEntity.ok(filteredScheduledRides.stream().map(RideHistoryDto::new).toList());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/scheduled/{id}/cancel")
    public ResponseEntity<RideHistoryDto> cancelScheduledRide(@PathVariable Long id, @RequestBody RideCancellationDto dto) {
        final User user = this.userService.getUser(dto.getUserId()).orElseThrow(RuntimeException::new);
        final Ride ride = this.rideService.getRide(id).orElseThrow(NotFoundException::new);
        final RideRequest rideRequest = ride.getRideRequest();
        rideRequest.setCancelledBy(user);
        rideRequest.setReason(dto.getMessage());
        ride.setRideRequest(this.rideRequestService.update(rideRequest));
        ride.setStatus(RideStatus.USER_CANCELLED);
        return ResponseEntity.ok(new RideHistoryDto(this.rideService.update(ride)));
    }
}
