package inc.visor.voom_service.route.dto;

import inc.visor.voom_service.shared.RoutePointDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RouteEstimateRequestDto {

  private List<RoutePointDto> routePoints;

  public RouteEstimateRequestDto() {
  }
}
