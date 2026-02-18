package inc.visor.voom_service.route.dto;

import inc.visor.voom_service.shared.RoutePointDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RouteEstimateRequestDto {

    @NotNull(message = "Route points are required")
    @Size(min = 2, max = 10, message = "There must be between 2 and 10 points")
    private List<RoutePointDto> routePoints;

    public RouteEstimateRequestDto() {
    }
}
