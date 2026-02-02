package inc.visor.voom_service.complaints.repository;

import inc.visor.voom_service.complaints.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository
        extends JpaRepository<Complaint, Long> {

    List<Complaint> findByRideId(Long rideId);
    List<Complaint> findByRide_Driver_Id(Long driverId);
}

