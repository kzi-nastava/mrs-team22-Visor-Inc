package inc.visor.voom_service.vehicle.repository;

import inc.visor.voom_service.vehicle.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleTypeRepository extends JpaRepository<VehicleType, Long> {
}
