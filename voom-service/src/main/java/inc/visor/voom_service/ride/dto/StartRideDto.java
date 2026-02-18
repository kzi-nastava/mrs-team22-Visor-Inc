package inc.visor.voom_service.ride.dto;

import inc.visor.voom_service.shared.RoutePointDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class StartRideDto {

    List<RoutePointDto> routePoints;

    @NotEmpty(message = "Route points are required")
    @Size(min = 2, message = "Route must contain at least pickup and dropoff")
    @Valid
    public List<RoutePointDto> getRoutePoints() {
        return routePoints;
    }

    public void setRoutePoints(List<RoutePointDto> routePoints) {
        this.routePoints = routePoints;
    }

}
