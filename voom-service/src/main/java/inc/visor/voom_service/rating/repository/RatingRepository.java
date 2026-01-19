package inc.visor.voom_service.rating.repository;

import inc.visor.voom_service.rating.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}

