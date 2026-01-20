package inc.visor.voom_service.ride.dto;

import java.util.List;

import inc.visor.voom_service.shared.RoutePointDto;

public class StartRideDto {

    List<RoutePointDto> routePoints;

    public List<RoutePointDto> getRoutePoints() {
        return routePoints;
    }

    public void setRoutePoints(List<RoutePointDto> routePoints) {
        this.routePoints = routePoints;
    }
    
}
