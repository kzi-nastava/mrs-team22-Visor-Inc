package inc.visor.voom_service.ride.dto;

import inc.visor.voom_service.shared.RoutePointDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CreateFavoriteRouteRequest {

    @NotBlank(message = "Route name is required")
    @Size(max = 100, message = "Route name must be at most 100 characters")
    private String name;

    @NotEmpty(message = "Route must contain at least two points")
    @Size(min = 2, message = "Route must contain at least pickup and dropoff")
    @Valid
    private List<RoutePointDto> points;

    public CreateFavoriteRouteRequest() {
    }

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
