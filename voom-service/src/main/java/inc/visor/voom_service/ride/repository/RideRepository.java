package inc.visor.voom_service.ride.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import inc.visor.voom_service.ride.model.Ride;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
}
