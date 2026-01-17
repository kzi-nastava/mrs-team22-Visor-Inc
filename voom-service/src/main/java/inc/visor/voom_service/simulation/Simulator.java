package inc.visor.voom_service.simulation;

import inc.visor.voom_service.driver.dto.DriverSummaryDto;
import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.osrm.dto.LatLng;
import inc.visor.voom_service.osrm.service.OsrmService;
import inc.visor.voom_service.shared.PredefinedRoutes;
import inc.visor.voom_service.shared.PredefinedRoutes.Route;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Simulator implements ApplicationRunner {

    private final DriverService driverService;
    private final OsrmService osrmService;
    private final SimulationState state;
    private final SimulationPublisher publisher;

    private final List<Route> predefinedRoutes = PredefinedRoutes.PREDEFINED_ROUTES;

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

        drivers.forEach(driver -> {
            Route route = predefinedRoutes.get((int) (driver.getId() % predefinedRoutes.size()));
            List<LatLng> waypoints = osrmService.getRoute(route.start(), route.end());
            state.add(new SimulatedDriver(driver, waypoints));
        });
    }

    @Scheduled(fixedRate = 3000)
    public void tick() {
        state.getAll().forEach(driver -> {
            LatLng pos = driver.nextPosition();
            if (pos != null) {
                publisher.publishPosition(driver.getDriverId(), pos);
            } else {
                publisher.publishPosition(driver.getDriverId(), driver.currentPosition());
            }
        });
    }

    public void changeDriverRoute(long driverId, double lat, double lng) {
        state.replaceRoute(driverId, new LatLng(lat, lng), osrmService);
    }
}
