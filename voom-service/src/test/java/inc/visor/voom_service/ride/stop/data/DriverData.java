package inc.visor.voom_service.ride.stop.data;

import inc.visor.voom_service.driver.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DriverData {

    DriverService driverService;

    @Autowired
    public DriverData(DriverService driverService) {
        this.driverService = driverService;
    }

}
