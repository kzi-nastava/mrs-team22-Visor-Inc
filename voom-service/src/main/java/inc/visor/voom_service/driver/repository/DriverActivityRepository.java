package inc.visor.voom_service.driver.repository;

import inc.visor.voom_service.driver.model.DriverStateChange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverActivityRepository extends JpaRepository<Long, DriverStateChange> {
}
