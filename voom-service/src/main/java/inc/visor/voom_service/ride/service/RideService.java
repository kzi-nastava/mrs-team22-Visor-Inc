package inc.visor.voom_service.ride.service;

import java.util.List;

import org.springframework.stereotype.Service;

import inc.visor.voom_service.ride.dto.RideLocationDto;
import inc.visor.voom_service.ride.model.Ride;

@Service
public class RideService {


        public void updateRidePosition(RideLocationDto dto) {
            // get and set position
            return;
        }

        public void finishRide(Ride ride) {
            // set status
            return;
        }

        public List<Ride> getDriverRides(Long driverId) {
            return null;
        }

}
