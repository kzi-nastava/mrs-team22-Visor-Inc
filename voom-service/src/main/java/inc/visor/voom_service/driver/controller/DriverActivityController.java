package inc.visor.voom_service.driver.controller;

import inc.visor.voom_service.driver.dto.DriverStateChangeDto;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.DriverStateChange;
import inc.visor.voom_service.driver.service.DriverActivityService;
import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activity")
public class DriverActivityController {

    DriverService driverService;
    DriverActivityService driverActivityService;
    Logger logger = LoggerFactory.getLogger(DriverActivityController.class);

    public DriverActivityController(DriverService driverService, DriverActivityService driverActivityService) {
        this.driverService = driverService;
        this.driverActivityService = driverActivityService;
    }

    @PostMapping
    public ResponseEntity<DriverStateChangeDto> changeDriverState(@RequestBody DriverStateChangeDto dto) {
        Driver driver = this.driverService.getDriverFromUser(dto.getUserId()).orElseThrow(NotFoundException::new);
        DriverStateChange driverState = new DriverStateChange(driver, dto);
        driverState = this.driverActivityService.create(driverState);
        return ResponseEntity.ok(new DriverStateChangeDto(driverState));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverStateChangeDto> getDriverState(@PathVariable long id) {
        Driver driver = this.driverService.getDriverFromUser(id).orElseThrow(NotFoundException::new);
        DriverStateChange lastStateChange = this.driverActivityService.getLastStateChange(driver.getId()).orElse(null);
        return ResponseEntity.ok(lastStateChange != null ? new DriverStateChangeDto(lastStateChange) : null);
    }

}
