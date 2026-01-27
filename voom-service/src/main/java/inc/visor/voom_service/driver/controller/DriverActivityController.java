package inc.visor.voom_service.driver.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import inc.visor.voom_service.driver.dto.DriverStateChangeDto;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.DriverStateChange;
import inc.visor.voom_service.driver.service.DriverActivityService;
import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.exception.NotFoundException;
import inc.visor.voom_service.simulation.Simulator;

@RestController
@RequestMapping("/api/activity")
public class DriverActivityController {

    DriverService driverService;
    DriverActivityService driverActivityService;
    Simulator simulationService;
    Logger logger = LoggerFactory.getLogger(DriverActivityController.class);

    public DriverActivityController(DriverService driverService, DriverActivityService driverActivityService, Simulator simulationService) {
        this.driverService = driverService;
        this.driverActivityService = driverActivityService;
        this.simulationService = simulationService;
    }

    @PostMapping
    public ResponseEntity<DriverStateChangeDto> changeDriverState(@RequestBody DriverStateChangeDto dto) {
        Driver driver = this.driverService.getDriverFromUser(dto.getUserId()).orElseThrow(NotFoundException::new);
        DriverStateChange driverState = new DriverStateChange(driver, dto);
        driverState = this.driverActivityService.create(driverState);
        if (dto.getCurrentState().equals("ACTIVE")) {
            System.out.println("Adding driver to simulation: " + driver.getId());
            simulationService.addActiveDriver(driver.getId());
        } else if (dto.getCurrentState().equals("INACTIVE")) {
            System.out.println("Removing driver from simulation: " + driver.getId());
            simulationService.removeActiveDriver(driver.getId());
        }
        return ResponseEntity.ok(new DriverStateChangeDto(driverState));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverStateChangeDto> getDriverState(@PathVariable long id) {
        Driver driver = this.driverService.getDriverFromUser(id).orElseThrow(NotFoundException::new);
        DriverStateChange lastStateChange = this.driverActivityService.getLastStateChange(driver.getId()).orElse(null);
        return ResponseEntity.ok(lastStateChange != null ? new DriverStateChangeDto(lastStateChange) : null);
    }

}
