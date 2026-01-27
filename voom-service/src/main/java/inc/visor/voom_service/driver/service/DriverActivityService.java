package inc.visor.voom_service.driver.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import inc.visor.voom_service.driver.model.DriverStateChange;
import inc.visor.voom_service.driver.repository.DriverActivityRepository;

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

    public List<DriverStateChange> findChangesSince(long driverId, java.time.LocalDateTime since) {
        return this.driverActivityRepository.findChangesSince(driverId, since);
    }

}
