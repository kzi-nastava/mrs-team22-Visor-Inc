package inc.visor.voom_service.ride.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.model.enums.RideStatus;


@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    Optional<Ride> findByRideRequestId(Long rideRequestId);
    List<Ride> findByDriverId(Long driverId);
    List<Ride> findByStatus(RideStatus status);
    List<Ride> findByStatusIn(List<RideStatus> statuses);
    Optional<Ride> getRideById(long id);

    List<Ride> findByRideRequest_Creator_Id(long rideRequestCreatorId);
    List<Ride> findByDriver_User_Id(Long userId);

}
