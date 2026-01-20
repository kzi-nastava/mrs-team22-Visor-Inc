package inc.visor.voom_service.simulation;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import inc.visor.voom_service.osrm.dto.LatLng;
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.model.RoutePoint;
import inc.visor.voom_service.ride.repository.RideRepository;


@Service
public class RideSimulationService {

    private final RideRepository rideRepository;

    private final Simulator simulator;
    private final ScheduledExecutorService executor =
        Executors.newSingleThreadScheduledExecutor();

    public RideSimulationService(RideRepository rideRepository, Simulator simulator) {
        this.rideRepository = rideRepository;
        this.simulator = simulator;
    }

    public void startRide(long driverId, List<LatLng> routePoints) {
        runSegment(driverId, routePoints, 0);
    }

    public void startRide(long driverId, long rideId) {
        Ride ride = rideRepository.findById(rideId).orElse(null);
        if (ride == null) return;

        List<LatLng> routePoints = ride.getRideRequest().getRideRoute().getRoutePoints()
                .stream()
                .sorted(Comparator.comparing(RoutePoint::getOrderIndex))
                .map(p -> new LatLng(p.getLatitude(), p.getLongitude()))
                .toList();

        startRide(driverId, routePoints);
    }

    private void runSegment(long driverId, List<LatLng> points, int index) {
        if (index >= points.size() - 1) {
            return; 
        }

        LatLng to = points.get(index + 1);

        simulator.changeDriverRoute(driverId, to.lat(), to.lng());

        executor.schedule(
            () -> runSegment(driverId, points, index + 1),
            3,
            TimeUnit.SECONDS
        );
    }
}
