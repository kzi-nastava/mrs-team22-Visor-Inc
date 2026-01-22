package inc.visor.voom_service.driver.controller;

import inc.visor.voom_service.driver.dto.DriverStateChangeDto;
import inc.visor.voom_service.driver.service.DriverActivityService;
import inc.visor.voom_service.driver.service.DriverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activity}")
public class DriverActivityController {

    DriverService driverService;
    DriverActivityService driverActivityService;


    @PostMapping
    public ResponseEntity<DriverStateChangeDto> changeDriverState(@RequestBody DriverStateChangeDto dto) {
        return ResponseEntity.ok(new DriverStateChangeDto());
    }

}
