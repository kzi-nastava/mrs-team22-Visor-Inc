package inc.visor.voom_service.ride.repository;

import inc.visor.voom_service.ride.model.FavoriteRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRouteRepository extends JpaRepository<FavoriteRoute, Long> {

    List<FavoriteRoute> findAllByUserId(long userId);
}
