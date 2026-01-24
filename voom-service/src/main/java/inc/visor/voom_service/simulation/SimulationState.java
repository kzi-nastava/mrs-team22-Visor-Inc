package inc.visor.voom_service.simulation;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

import inc.visor.voom_service.driver.dto.DriverSummaryDto;
import inc.visor.voom_service.shared.PredefinedRoutes;
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

        List<LatLng> newWaypoints = osrm.getRoute(existing.currentPosition(), newEnd);

        drivers.remove(existing);
        drivers.add(new SimulatedDriver(existing.getDriver(), newWaypoints));
    }

    public void syncDriversWithDatabase(List<DriverSummaryDto> dbDrivers, OsrmService osrm, List<PredefinedRoutes.Route> predefinedRoutes) {
        List<Long> dbIds = dbDrivers.stream().map(DriverSummaryDto::getId).toList();

        drivers.removeIf(d -> !dbIds.contains(d.getDriverId()));

        dbDrivers.forEach(dbDriver -> {
            boolean exists = drivers.stream().anyMatch(d -> d.getDriverId() == dbDriver.getId());

            if (!exists) {
                CompletableFuture.supplyAsync(() -> {
                    PredefinedRoutes.Route route = predefinedRoutes.get((int) (dbDriver.getId() % predefinedRoutes.size()));
                    return osrm.getRoute(route.start(), route.end());
                }).thenAccept(waypoints -> {
                    this.add(new SimulatedDriver(dbDriver, waypoints));
                    System.out.println("Route ready for driver: " + dbDriver.getId());
                }).exceptionally(ex -> {
                    System.err.println("Failed to fetch route: " + ex.getMessage());
                    return null;
                });
            }
        });
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

    public void setFinishedRide(long driverId) {
        SimulatedDriver existing = drivers.stream()
                .filter(d -> d.getDriverId() == driverId)
                .findFirst()
                .orElse(null);

        if (existing == null) return;

        existing.setFinishedRide(true);
    }
}

