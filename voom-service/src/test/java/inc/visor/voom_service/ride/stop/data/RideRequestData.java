package inc.visor.voom_service.ride.stop.data;

import inc.visor.voom_service.ride.service.RideRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RideRequestData {

    RideRequestService rideRequestService;

    @Autowired
    public RideRequestData(RideRequestService rideRequestService) {
        this.rideRequestService = rideRequestService;
    }



}
