package inc.visor.voom_service.simulation;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import inc.visor.voom_service.driver.dto.DriverSummaryDto;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.osrm.dto.LatLng;
import inc.visor.voom_service.osrm.service.OsrmService;
import inc.visor.voom_service.shared.PredefinedRoutes;
import inc.visor.voom_service.shared.PredefinedRoutes.Route;

@Order(2)
@Service
public class Simulator implements ApplicationRunner {

    private final DriverService driverService;
    private final OsrmService osrmService;
    private final SimulationState state;
    private final SimulationPublisher publisher;


    private final List<Route> predefinedRoutes = PredefinedRoutes.PREDEFINED_ROUTES;

    private volatile boolean initialized = false;

    public Simulator(
            DriverService driverService,
            OsrmService osrmService,
            SimulationState state,
            SimulationPublisher publisher
    ) {
        this.driverService = driverService;
        this.osrmService = osrmService;
        this.state = state;
        this.publisher = publisher;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<DriverSummaryDto> drivers = driverService.getActiveDrivers();
        System.out.println("Driver size: on run: " + drivers.size());
        drivers.forEach(driver -> {
            Route route = predefinedRoutes.get((int) (driver.getId() % predefinedRoutes.size()));
            List<LatLng> waypoints = osrmService.getRoute(route.start(), route.end());
            state.add(new SimulatedDriver(driver, waypoints));
        });
        this.initialized = true;

    }

    @Scheduled(fixedRate = 3000)
    public void tick() {

        if (!initialized) {
            return;
        }

//        List<DriverSummaryDto> driversFromDb = driverService.getActiveDrivers();

        // state.syncDriversWithDatabase(driversFromDb, osrmService, predefinedRoutes);

        List<SimulatedDriver> activeDrivers = state.getAll();
        System.out.println("tick - active drivers: " + activeDrivers.size());

        state.getAll().forEach(driver -> {
            LatLng pos = driver.nextPosition();
            int eta = (driver.getWaypointCount() - driver.getWaypointIndex()) * 3;
            if (pos != null) {
                publisher.publishPosition(driver.getDriverId(), pos, driver.isFinishedRide(), eta);
            } else {
                publisher.publishPosition(driver.getDriverId(), driver.currentPosition(), driver.isFinishedRide(), eta);
            }
        });
    }

    public boolean addActiveDriver(long driverId) {
        if (!state.existsDriver(driverId)) {
            System.out.println("Adding active driver to simulation: " + driverId);
            Optional<Driver> dbDriver = driverService.getDriver(driverId);
            if (dbDriver.isPresent()) {
                System.out.println("Driver found in DB, adding to simulation: " + driverId);
                DriverSummaryDto dto = new DriverSummaryDto(dbDriver.get());
                Route route = predefinedRoutes.get((int) (dto.getId() % predefinedRoutes.size()));
                List<LatLng> waypoints = osrmService.getRoute(route.start(), route.end());
                SimulatedDriver newDriver = new SimulatedDriver(dto, waypoints);
                state.add(newDriver);
                return true;
            }
            System.out.println("Driver not found in DB, cannot add to simulation: " + driverId);
            return false;
        }
        return false;
    }

    public boolean removeActiveDriver(long driverId) {
        if (!state.existsDriver(driverId)) {
            return false;
        }

        state.removeDriver(driverId);
        return true;

    }

    public void changeDriverRoute(long driverId, double lat, double lng) {
        state.replaceRoute(driverId, new LatLng(lat, lng), osrmService);
    }

    public void changeDriverRouteMultiplePoints(long driverId, List<LatLng> newPoints) {
        state.replaceRouteMultiplePoints(driverId, newPoints, osrmService);
    }

    public void setFinishedRide(long driverId) {
        state.setFinishedRide(driverId);
    }

}
