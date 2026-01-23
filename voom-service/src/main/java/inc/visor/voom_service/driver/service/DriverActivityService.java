package inc.visor.voom_service.driver.service;

import inc.visor.voom_service.driver.model.DriverStateChange;
import inc.visor.voom_service.driver.repository.DriverActivityRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DriverActivityService {

    DriverActivityRepository driverActivityRepository;

    public DriverActivityService(DriverActivityRepository driverActivityRepository) {
        this.driverActivityRepository = driverActivityRepository;
    }

    public DriverStateChange create(DriverStateChange driverStateChange) {
        return this.driverActivityRepository.save(driverStateChange);
    }

    public Optional<DriverStateChange> getLastStateChange(long driverId) {
        return this.driverActivityRepository.getLastStateChange(driverId);
    }

}
