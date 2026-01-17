package inc.visor.voom_service.ride.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import inc.visor.voom_service.ride.model.FavoriteRoute;

public interface FavoriteRouteRepository extends JpaRepository<FavoriteRoute, Long> {

    List<FavoriteRoute> findAllByUserId(long userId);
}
