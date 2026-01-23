package inc.visor.voom_service.driver.repository;

import inc.visor.voom_service.driver.model.DriverStateChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DriverActivityRepository extends JpaRepository<DriverStateChange, Long> {

    @Query("SELECT dsc FROM DriverStateChange dsc WHERE dsc.driver.id = :driverId ORDER BY dsc.performedAt DESC LIMIT 1")
    Optional<DriverStateChange> getLastStateChange(long driverId);
}
