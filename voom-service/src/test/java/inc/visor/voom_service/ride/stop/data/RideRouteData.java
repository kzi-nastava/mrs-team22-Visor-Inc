package inc.visor.voom_service.ride.stop.data;

import inc.visor.voom_service.ride.model.RideRoute;
import inc.visor.voom_service.ride.model.RoutePoint;
import inc.visor.voom_service.route.service.RideRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RideRouteData {

    RideRouteService rideRouteService;

    @Autowired
    public RideRouteData(RideRouteService rideRouteService) {
        this.rideRouteService = rideRouteService;
    }

    public class RideRouteBuilder {
        private final RideRoute rideRoute;

        public RideRouteBuilder(RideRoute rideRoute) {
            this.rideRoute = rideRoute;
        }

        public RideRouteBuilder withId(long id) {
            this.rideRoute.setId(id);
            return this;
        }

        public RideRouteBuilder withTotalDistanceKm(double distanceKm) {
            this.rideRoute.setTotalDistanceKm(distanceKm);
            return this;
        }

        public RideRouteBuilder withRoutePoints(List<RoutePoint> routePoints) {
            this.rideRoute.setRoutePoints(routePoints);
            return this;
        }

        public RideRoute build() {
            return rideRouteService.create(rideRoute);
        }

    }

}
