package inc.visor.voom_service.route.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import inc.visor.voom_service.ride.model.RideRoute;


@Repository
public interface RideRouteRepository extends JpaRepository<RideRoute, Long> {
    
}
