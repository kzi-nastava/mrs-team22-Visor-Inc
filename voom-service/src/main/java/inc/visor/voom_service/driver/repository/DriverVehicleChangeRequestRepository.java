package inc.visor.voom_service.driver.repository;

import inc.visor.voom_service.driver.dto.VehicleChangeRequestStatus;
import inc.visor.voom_service.driver.model.DriverVehicleChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverVehicleChangeRequestRepository
        extends JpaRepository<DriverVehicleChangeRequest, Long> {

    Optional<DriverVehicleChangeRequest> findByDriverIdAndStatus(long driverId, VehicleChangeRequestStatus status);

    Optional<DriverVehicleChangeRequest> findByStatus(VehicleChangeRequestStatus status);
}
