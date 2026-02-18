package inc.visor.voom_service.driver.controller;

import inc.visor.voom_service.activation.service.ActivationTokenService;
import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.model.VoomUserDetails;
import inc.visor.voom_service.auth.user.service.UserService;
import inc.visor.voom_service.driver.dto.*;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.DriverStatus;
import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.exception.NotFoundException;
import inc.visor.voom_service.person.model.Person;
import inc.visor.voom_service.person.service.PersonService;
import inc.visor.voom_service.ride.dto.ActiveRideDto;
import inc.visor.voom_service.ride.dto.RideResponseDto;
import inc.visor.voom_service.simulation.Simulator;
import inc.visor.voom_service.vehicle.dto.VehicleSummaryDto;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private static final Logger logger = LoggerFactory.getLogger(DriverController.class);
    private final DriverService driverService;
    private final ActivationTokenService activationTokenService;
    private final UserService userService;
    private final PersonService personService;
    private final Simulator simulationService;

    public DriverController(DriverService driverService, ActivationTokenService activationTokenService, UserService userService, PersonService personService, Simulator simulationService) {
        this.driverService = driverService;
        this.activationTokenService = activationTokenService;
        this.userService = userService;
        this.personService = personService;
        this.simulationService = simulationService;
    }

    @PostMapping
    public ResponseEntity<DriverSummaryDto> createDriver(@Valid @RequestBody CreateDriverDto request) {
        System.out.println("RAW BODY:");
        System.out.println("EMAIL: " + request.getEmail());
        System.out.println("BIRTHDATE: " + request.getBirthDate());
        System.out.println("VEHICLE: " + request.getVehicle());

        Driver driver = driverService.createDriver(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new DriverSummaryDto(driver));
    }

    @PostMapping("/admin")
    public ResponseEntity<DriverSummaryDto> adminCreateDriver(@Valid @RequestBody AdminCreateDriverDto dto) {
        User user = this.userService.getUser(dto.getUserId()).orElseThrow(NotFoundException::new);
        Driver driver = new Driver(user, DriverStatus.AVAILABLE);
        driver = driverService.create(driver);
        return ResponseEntity.ok(new DriverSummaryDto(driver));
    }

    // we would use message mapping in real world if driver location came from actual driver and not from simulation
//    @MessageMapping("/drivers.updateLocation")
//    @SendTo("/topic/driversLocations")
//    public DriverLocationDto updateLocation(@Payload DriverLocationDto request) {
//        return request;
//    }
    @GetMapping("/active")
    public ResponseEntity<List<DriverSummaryDto>> getActiveDrivers() {
        List<DriverSummaryDto> response = driverService.getActiveDrivers();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DriverSummaryDto>> getDrivers() {
        return ResponseEntity.ok(driverService.getDrivers().stream().map(DriverSummaryDto::new).toList());
    }

    @GetMapping("/{driverId}")
    public ResponseEntity<DriverSummaryDto> getDriver(@PathVariable Long driverId) {
        Driver driver = this.driverService.getDriver(driverId).orElseThrow(NotFoundException::new);
        return ResponseEntity.ok(new DriverSummaryDto(driver));
    }

    @PutMapping("/{driverId}")
    public ResponseEntity<DriverSummaryDto> updateDriver(@PathVariable Long driverId, @RequestBody DriverSummaryDto driverSummaryDto) {
        Driver driver = this.driverService.getDriver(driverId).orElseThrow(NotFoundException::new);
        logger.info("Dto: {}", driverSummaryDto);
        Person person = new Person(driver.getUser().getPerson().getId(), driverSummaryDto);
        User user = new User(driver.getUser().getId(), driverSummaryDto);
        logger.info("User: {}", user);
        logger.info("Person: {}", person);
        this.personService.update(person);
        this.userService.update(user);
        return ResponseEntity.ok(driverSummaryDto);
    }

    @DeleteMapping("/{driverId}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long driverId) {
        this.driverService.getDriver(driverId).orElseThrow(NotFoundException::new);
        this.driverService.deleteDriver(driverId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<VehicleSummaryDto> getMyDriverInfo(@AuthenticationPrincipal VoomUserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        User user = userService.getUser(username).orElseThrow(NotFoundException::new);

        Long userId = user.getId();

        return ResponseEntity.ok(driverService.getVehicle(userId));
    }

    @PutMapping("/me")
    public ResponseEntity<Void> requestVehicleUpdate(
            @AuthenticationPrincipal VoomUserDetails userDetails,
            @Valid @RequestBody VehicleSummaryDto request
    ) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        User user = userService.getUser(username).orElseThrow(NotFoundException::new);

        driverService.createVehicleChangeRequest(user.getId(), request);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{driverId}/report")
    public ResponseEntity<Void> reportDriver(@PathVariable Long driverId, @RequestBody ReportDriverRequestDto req, @AuthenticationPrincipal User user) {
        // get needed info and pass into DriverService
        // driverService.reportDriver(driverId, req.getComment(), user.getId())
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{driverId}/history")
    public ResponseEntity<List<RideResponseDto>> getRideHistory(@PathVariable Long driverId, @AuthenticationPrincipal User user) {
        List<RideResponseDto> response = new ArrayList<>();

        // if (user is not driver) -> obliterate him { return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/activation")
    public ResponseEntity<String> activateDriver(
            @Valid @RequestBody ActivateDriverRequestDto request
    ) {

        activationTokenService.activateAccount(
                request.getToken(),
                request.getPassword(),
                request.getConfirmPassword()
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active-ride")
    public ResponseEntity<ActiveRideDto> getActiveRide(@AuthenticationPrincipal VoomUserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        User user = this.userService.getUser(username).orElseThrow(NotFoundException::new);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = user.getId();

        ActiveRideDto response = driverService.getActiveRide(userId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/remove-simulation")
    public ResponseEntity<Void> removeDriverFromSimulation(
            @AuthenticationPrincipal VoomUserDetails userDetails
    ) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        User user = this.userService.getUser(username).orElseThrow(NotFoundException::new);

        long userId = user.getId();

        Driver driver = driverService.getDriverFromUser(userId).orElseThrow(NotFoundException::new);

        simulationService.removeActiveDriver(driver.getId());

        return ResponseEntity.noContent().build();
    }

}
