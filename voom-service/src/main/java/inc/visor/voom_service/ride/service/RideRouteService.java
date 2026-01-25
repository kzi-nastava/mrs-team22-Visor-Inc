package inc.visor.voom_service.ride.service;

import inc.visor.voom_service.ride.model.RideRoute;
import inc.visor.voom_service.ride.repository.RideRouteRepository;
import org.springframework.stereotype.Service;

@Service
public class RideRouteService {

    RideRouteRepository rideRouteRepository;

    public RideRouteService(RideRouteRepository rideRouteRepository) {
        this.rideRouteRepository = rideRouteRepository;
    }

    public RideRoute create(RideRoute rideRoute) {
        return this.rideRouteRepository.save(rideRoute);
    }

    public RideRoute update(RideRoute rideRoute) {
        return this.rideRouteRepository.save(rideRoute);
    }

}
