package inc.visor.voom_service.ride.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import inc.visor.voom_service.ride.model.RideRequest;

@Repository
public interface RideRequestRepository extends JpaRepository<RideRequest, Long> {

    @Query("""
    SELECT rr FROM RideRequest rr
    WHERE rr.scheduleType = inc.visor.voom_service.ride.model.enums.ScheduleType.LATER
      AND rr.scheduledTime BETWEEN :now AND :threshold
""")
    List<RideRequest> findUpcomingScheduled(
            @Param("now") LocalDateTime now,
            @Param("threshold") LocalDateTime threshold
    );

}
