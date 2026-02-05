package inc.visor.voom.app.shared.dto.route;

import java.util.List;

import inc.visor.voom.app.shared.dto.RoutePointDto;
import inc.visor.voom.app.user.home.model.RoutePoint;

public class RouteEstimateRequestDto {
    private List<RoutePoint> routePoints;

    public RouteEstimateRequestDto(List<RoutePoint> routePoints) {
        this.routePoints = routePoints;
    }

}
