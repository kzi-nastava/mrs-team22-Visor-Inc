package inc.visor.voom_service.route.dto;

import java.util.List;

import inc.visor.voom_service.shared.RoutePointDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class CreateFavoriteRouteRequestDto {

    @NotBlank
    private String name;

    @NotEmpty
    @Size(min = 2)
    private List<RoutePointDto> routePoints;

    public CreateFavoriteRouteRequestDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RoutePointDto> getRoutePoints() {
        return routePoints;
    }

    public void setRoutePoints(List<RoutePointDto> routePoints) {
        this.routePoints = routePoints;
    }
}
