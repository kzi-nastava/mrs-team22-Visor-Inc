package inc.visor.voom_service.driver.repository;

import inc.visor.voom_service.driver.model.DriverStateChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DriverActivityRepository extends JpaRepository<DriverStateChange, Long> {

    @Query("SELECT dsc FROM DriverStateChange dsc WHERE dsc.driver.id = :driverId ORDER BY dsc.performedAt DESC LIMIT 1")
    Optional<DriverStateChange> getLastStateChange(long driverId);

    @Query("""
            SELECT dsc
            FROM DriverStateChange dsc
            WHERE dsc.driver.id = :driverId
            AND dsc.performedAt >= :since
            ORDER BY dsc.performedAt ASC
            """)
    List<DriverStateChange> findChangesSince(
            @Param("driverId") Long driverId,
            @Param("since") LocalDateTime since
    );

    @Query("""
            SELECT dsc
            FROM DriverStateChange dsc
            WHERE dsc.driver.id = :driverId
            AND dsc.performedAt < :before
            ORDER BY dsc.performedAt DESC
            """)
    List<DriverStateChange> findLastChangeBefore(
            @Param("driverId") Long driverId,
            @Param("before") LocalDateTime before
    );

}
