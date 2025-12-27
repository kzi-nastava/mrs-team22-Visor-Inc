package inc.visor.voom_service.driver.service;

import inc.visor.voom_service.driver.dto.DriverLocationDto;
import org.springframework.stereotype.Service;

@Service
public class DriverService {
    public void simulateMove(DriverLocationDto dto) {
        // used for simulating movement
        // one possible way is to 1. select 2 random points in Novi Sad
        // 2. call external api to get route and waypoints between these two
        // 3. update position for each waypoint that api returns
        // 4. broadcast update
        return;
    }

    public void reportDriver(Long driverId, Long userId, String comment) {
        return;
    }

}
