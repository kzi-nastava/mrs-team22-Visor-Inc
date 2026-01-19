package inc.visor.voom_service.ride.repository;

import inc.visor.voom_service.ride.model.RideReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RideReportRepository
        extends JpaRepository<RideReport, Long> {
}

