package inc.visor.voom_service.rating.repository;

import inc.visor.voom_service.rating.model.DriverRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRatingRepository extends JpaRepository<DriverRating,Long> {
}
