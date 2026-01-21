package inc.visor.voom_service.driver.controller;

import inc.visor.voom_service.activation.service.ActivationTokenService;
import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.service.UserService;
import inc.visor.voom_service.driver.dto.ActivateDriverRequestDto;
import inc.visor.voom_service.driver.dto.CreateDriverDto;
import inc.visor.voom_service.driver.dto.DriverSummaryDto;
import inc.visor.voom_service.driver.dto.ReportDriverRequestDto;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.exception.NotFoundException;
import inc.visor.voom_service.person.model.Person;
import inc.visor.voom_service.person.service.PersonService;
import inc.visor.voom_service.ride.dto.RideResponseDto;
import inc.visor.voom_service.vehicle.dto.VehicleSummaryDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;
    private final ActivationTokenService activationTokenService;
    private final UserService userService;
    private final PersonService personService;

    public DriverController(DriverService driverService, ActivationTokenService activationTokenService, UserService userService, PersonService personService) {
        this.driverService = driverService;
        this.activationTokenService = activationTokenService;
        this.userService = userService;
        this.personService = personService;
    }

    @PostMapping
    public ResponseEntity<DriverSummaryDto> createDriver(@Valid @RequestBody CreateDriverDto request) {
        Driver driver = driverService.createDriver(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new DriverSummaryDto(driver));
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
    public ResponseEntity<DriverSummaryDto> updateDriver(@PathVariable Long driverId, DriverSummaryDto driverSummaryDto) {
        Driver driver = this.driverService.getDriver(driverId).orElseThrow(NotFoundException::new);
        Person person = new Person(driver.getUser().getPerson().getId(), driverSummaryDto);
        User user = new User(driver.getUser().getId(), driverSummaryDto);
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
    public ResponseEntity<VehicleSummaryDto> getMyDriverInfo(@AuthenticationPrincipal User user) {
        Long userId = (user != null) ? user.getId() : 2L;

        return ResponseEntity.ok(driverService.getVehicle(userId));
    }

    @PutMapping("/me")
    public ResponseEntity<VehicleSummaryDto> updateMyDriverInfo(@AuthenticationPrincipal User user, @RequestBody VehicleSummaryDto request) {
        Long userId = (user != null) ? user.getId() : 2L;

        return ResponseEntity.ok(driverService.updateVehicle(userId, request));
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

        activationTokenService.activateAccount(
                request.getToken(),
                request.getPassword(),
                request.getConfirmPassword()
        );

        return ResponseEntity.noContent().build();
    }

}
