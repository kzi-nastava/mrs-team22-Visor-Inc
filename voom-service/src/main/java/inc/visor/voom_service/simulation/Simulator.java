package inc.visor.voom_service.simulation;

import inc.visor.voom_service.driver.dto.DriverSummaryDto;
import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.osrm.dto.LatLng;
import inc.visor.voom_service.osrm.service.OsrmService;
import inc.visor.voom_service.shared.PredefinedRoutes;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import inc.visor.voom_service.shared.PredefinedRoutes.Route;

@Service
public class Simulator implements ApplicationRunner {


    private record LocationDto (
        long driverId,
        double lat,
        double lng) {}


    private class SimulatedDriver {
        DriverSummaryDto driver;
        int waypointIndex;
        LatLng location;
        List<LatLng> waypoints;

        public SimulatedDriver(DriverSummaryDto driver, int waypointIndex, LatLng location, List<LatLng> waypoints) {
            this.driver = driver;
            this.waypointIndex = waypointIndex;
            this.location = location;
            this.waypoints = waypoints;
        }

        public SimulatedDriver(DriverSummaryDto driver, int waypointIndex, LatLng location) {
            this.driver = driver;
            this.waypointIndex = waypointIndex;
            this.location = location;
        }
    }


    List<DriverSummaryDto> drivers;
    DriverService driverService;
    List<SimulatedDriver> simDrivers = new ArrayList<SimulatedDriver>();

    private final OsrmService osrmService;

    private final List<Route> predefinedRoutes = PredefinedRoutes.PREDEFINED_ROUTES;

    private final SimpMessagingTemplate messaging;


    public Simulator(OsrmService osrmService, DriverService driverService, SimpMessagingTemplate messaging) {
        this.osrmService = osrmService;
        this.driverService = driverService;
        this.drivers = driverService.getActiveDrivers();
        this.messaging = messaging;
    }

    public void run(ApplicationArguments args) throws Exception {

        for (DriverSummaryDto driver : drivers) {
            int index = (int) (driver.getId() % predefinedRoutes.size());
            Route route = predefinedRoutes.get(index);
            System.out.println("Starting simulation for driver " + driver.getId());

            simulateRoute(driver, route.start(), route.end());
        }
    }

    public void simulateRoute(DriverSummaryDto driver, LatLng start, LatLng end) {
        List<LatLng> waypoints = osrmService.getRoute(start, end);

        SimulatedDriver simDriver = new SimulatedDriver(driver, 0, start, waypoints);
        simDrivers.add(simDriver);

    }

    @Scheduled(fixedRate = 2000)
    public void tick() {

        for (SimulatedDriver d : simDrivers) {

            if (d.waypointIndex >= d.waypoints.size()) {
                continue; // driver finished route
            }
            LatLng pos = d.waypoints.get(d.waypointIndex);

            messaging.convertAndSend(
                    "/topic/drivers-positions",
                    new LocationDto(
                            d.driver.getId(),
                            pos.lat(),
                            pos.lng()
                    )
            );

            d.waypointIndex++;
        }
    }

}
