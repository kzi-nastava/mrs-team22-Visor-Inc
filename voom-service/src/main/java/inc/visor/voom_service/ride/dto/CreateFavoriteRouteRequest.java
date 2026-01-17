package inc.visor.voom_service.ride.dto;

import java.util.List;

import inc.visor.voom_service.shared.RoutePointDto;

public class CreateFavoriteRouteRequest {

    private String name;
    private List<RoutePointDto> points;

    public CreateFavoriteRouteRequest() {  }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RoutePointDto> getPoints() {
        return points;
    }

    public void setPoints(List<RoutePointDto> points) {
        this.points = points;
    }
}
