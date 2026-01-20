package inc.visor.voom_service.simulation;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;

import inc.visor.voom_service.osrm.dto.LatLng;
import inc.visor.voom_service.osrm.service.OsrmService;

@Component
public class SimulationState {

    private final List<SimulatedDriver> drivers = new CopyOnWriteArrayList<>();

    public void add(SimulatedDriver driver) {
        drivers.add(driver);
    }

    public List<SimulatedDriver> getAll() {
        return drivers;
    }

    public void replaceRoute(long driverId, LatLng newEnd, OsrmService osrm) {

        SimulatedDriver existing = drivers.stream()
                .filter(d -> d.getDriverId() == driverId)
                .findFirst()
                .orElse(null);

        if (existing == null) return;

        drivers.remove(existing);

        List<LatLng> newWaypoints =
                osrm.getRoute(existing.currentPosition(), newEnd);

        drivers.add(new SimulatedDriver(existing.getDriver(), newWaypoints));
    }

    public void replaceRouteMultiplePoints(long driverId, List<LatLng> newPoints, OsrmService osrm) {
        SimulatedDriver existing = drivers.stream()
                .filter(d -> d.getDriverId() == driverId)
                .findFirst()
                .orElse(null);

        if (existing == null) return;
        drivers.remove(existing);

        List<LatLng> newWaypoints =
                osrm.getRoute(newPoints);

        drivers.add(new SimulatedDriver(existing.getDriver(), newWaypoints));
    }
    
}

