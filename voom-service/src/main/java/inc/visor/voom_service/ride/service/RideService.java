package inc.visor.voom_service.ride.service;

import java.util.List;

import org.springframework.stereotype.Service;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.ride.dto.CreateRideRequestDto;
import inc.visor.voom_service.ride.dto.RideLocationDto;
import inc.visor.voom_service.ride.dto.RideRequestResponseDto;
import inc.visor.voom_service.ride.model.Ride;

@Service
public class RideService {

        public RideRequestResponseDto createRideRequest(User user, CreateRideRequestDto request) {
            return null;
        }

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
