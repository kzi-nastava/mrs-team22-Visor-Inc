package inc.visor.voom_service.driver.service;

import inc.visor.voom_service.driver.repository.DriverActivityRepository;
import org.springframework.stereotype.Service;

@Service
public class DriverActivityService {

    DriverActivityRepository driverActivityRepository;

    public DriverActivityService(DriverActivityRepository driverActivityRepository) {
        this.driverActivityRepository = driverActivityRepository;
    }

    

}
