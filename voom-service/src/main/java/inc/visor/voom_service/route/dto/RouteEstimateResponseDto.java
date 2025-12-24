package inc.visor.voom_service.route.dto;

import inc.visor.voom_service.ride.dto.RoutePointDto;
import java.util.List;

public class RouteEstimateResponseDto {

  private int duration;
  private List<RoutePointDto> routePoints;

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public List<RoutePointDto> getRoutePoints() {
    return routePoints;
  }

  public void setRoutePoints(List<RoutePointDto> routePoints) {
    this.routePoints = routePoints;
  }
}
