package inc.visor.voom_service.route.service;

import inc.visor.voom_service.ride.model.RideRoute;
import org.springframework.stereotype.Service;

import inc.visor.voom_service.route.repository.RideRouteRepository;

@Service
public class RideRouteService {

    private final RideRouteRepository rideRouteRepository;

    public RideRouteService(RideRouteRepository rideRouteRepository) {
        this.rideRouteRepository = rideRouteRepository;
    }

    public RideRoute create(RideRoute rideRoute) {
        return this.rideRouteRepository.save(rideRoute);
    }

    public RideRoute update(RideRoute rideRoute) {
        return this.rideRouteRepository.save(rideRoute);
    }

    public long estimateDurationInMinutes(double distanceInKm) {
        double averageSpeedKmh = 40.0;
        double hours = distanceInKm / averageSpeedKmh;
        return Math.round(hours * 60);
    }
}