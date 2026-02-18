package inc.visor.voom_service.simulation;

import inc.visor.voom_service.driver.dto.DriverSummaryDto;
import inc.visor.voom_service.osrm.dto.LatLng;
import inc.visor.voom_service.osrm.service.OsrmService;
import inc.visor.voom_service.shared.PredefinedRoutes;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SimulationState {

    private final List<SimulatedDriver> drivers = new CopyOnWriteArrayList<>();
    private final Set<Long> pendingDrivers = ConcurrentHashMap.newKeySet();

    public void add(SimulatedDriver driver) {
        drivers.add(driver);
    }

    public List<SimulatedDriver> getAll() {
        return drivers;
    }

    public void replaceRoute(long driverId, LatLng newEnd, OsrmService osrm) {
        SimulatedDriver existing = drivers.stream()
                .filter(d -> d.getDriver().getId() == driverId)
                .findFirst()
                .orElse(null);

        if (existing == null) return;

        List<LatLng> newWaypoints = osrm.getRoute(existing.currentPosition(), newEnd);

        drivers.remove(existing);
        drivers.add(new SimulatedDriver(existing.getDriver(), newWaypoints));
    }


    public void syncDriversWithDatabase(List<DriverSummaryDto> dbDrivers, OsrmService osrm, List<PredefinedRoutes.Route> predefinedRoutes) {
        List<Long> dbIds = dbDrivers.stream()
                .map(DriverSummaryDto::getId)
                .toList();


        drivers.removeIf(d -> !dbIds.contains(d.getDriver().getId()));

        dbDrivers.forEach(dbDriver -> {
            long driverId = dbDriver.getId();

            boolean exists = drivers.stream()
                    .anyMatch(d -> d.getDriver().getId() == driverId);

            PredefinedRoutes.Route predefinedRoute = predefinedRoutes.get((int) (driverId % predefinedRoutes.size()));

            if (!exists) {
                drivers.add(new SimulatedDriver(dbDriver, osrm.getRoute(predefinedRoute.start(), predefinedRoute.end())));
            }
        });
    }

    public void replaceRouteMultiplePoints(long driverId, List<LatLng> newPoints, OsrmService osrm) {
        SimulatedDriver existing = drivers.stream()
                .filter(d -> d.getDriver().getId() == driverId)
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
                .filter(d -> d.getDriver().getId() == driverId)
                .findFirst()
                .orElse(null);

        if (existing == null) return;

        existing.setFinishedRide(true);
    }

    public boolean existsDriver(long driverId) {
        return drivers.stream().anyMatch(d -> d.getDriver().getId() == driverId);
    }

    public void removeDriver(long driverId) {
        drivers.removeIf(d -> d.getDriver().getId() == driverId);
    }
}

