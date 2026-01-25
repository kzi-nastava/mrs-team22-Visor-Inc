package inc.visor.voom_service.simulation;

import inc.visor.voom_service.driver.dto.DriverSummaryDto;
import inc.visor.voom_service.osrm.dto.LatLng;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SimulatedDriver {

    private final DriverSummaryDto driver;
    private final List<LatLng> waypoints;
    private int waypointIndex = 0;
    private boolean finishedRide = false;

    public SimulatedDriver(DriverSummaryDto driver, List<LatLng> waypoints) {
        this.driver = driver;
        this.waypoints = waypoints;
    }

    public LatLng nextPosition() {
        if (waypointIndex >= waypoints.size()) return null;
        return waypoints.get(waypointIndex++);
    }

    public LatLng currentPosition() {
        if (waypointIndex == 0) return waypoints.getFirst();
        return waypoints.get(Math.min(waypointIndex - 1, waypoints.size() - 1));
    }
}
