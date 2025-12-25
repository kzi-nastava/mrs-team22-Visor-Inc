package inc.visor.voom_service.rating.repository;

import inc.visor.voom_service.rating.model.VehicleRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRatingRepository extends JpaRepository<VehicleRating,Long> {
}
