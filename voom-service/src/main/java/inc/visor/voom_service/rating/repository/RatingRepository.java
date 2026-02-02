package inc.visor.voom_service.rating.repository;

import inc.visor.voom_service.rating.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    @Query("SELECT r FROM Rating r JOIN FETCH r.rater WHERE r.ride.id = :rideId")
    List<Rating> findByRideId(Long rideId);

    @Query("SELECT r FROM Rating r JOIN FETCH r.rater WHERE r.ride.driver.id = :driverId")
    List<Rating> findByRide_Driver_Id(Long driverId);
}

