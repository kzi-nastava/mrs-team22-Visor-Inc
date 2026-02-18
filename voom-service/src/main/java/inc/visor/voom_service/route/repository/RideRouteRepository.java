package inc.visor.voom_service.route.repository;

import inc.visor.voom_service.ride.model.RideRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RideRouteRepository extends JpaRepository<RideRoute, Long> {

}
