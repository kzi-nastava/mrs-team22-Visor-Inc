package inc.visor.voom_service.ride.stop.data;

import inc.visor.voom_service.ride.service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RideData {

    RideService rideService;

    @Autowired
    public RideData(RideService rideService) {
        this.rideService = rideService;
    }



}
