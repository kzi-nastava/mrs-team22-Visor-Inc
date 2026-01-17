package inc.visor.voom_service.simulation;

import inc.visor.voom_service.driver.dto.DriverSummaryDto;
import inc.visor.voom_service.osrm.dto.LatLng;

import java.util.List;

public class SimulatedDriver {

    private final DriverSummaryDto driver;
    private final List<LatLng> waypoints;
    private int waypointIndex = 0;

    public SimulatedDriver(DriverSummaryDto driver, List<LatLng> waypoints) {
        this.driver = driver;
        this.waypoints = waypoints;
    }

    public long getDriverId() {
        return driver.getId();
    }

    public DriverSummaryDto getDriver() {
        return driver;
    }

    public LatLng nextPosition() {
        if (waypointIndex >= waypoints.size()) return null;
        return waypoints.get(waypointIndex++);
    }

    public LatLng currentPosition() {
        if (waypointIndex == 0) return waypoints.get(0);
        return waypoints.get(Math.min(waypointIndex - 1, waypoints.size() - 1));
    }
}
