package inc.visor.voom_service.route.dto;

import inc.visor.voom_service.shared.RoutePointDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RouteEstimateResponseDto {

  private int duration;
  private double distance;

  public RouteEstimateResponseDto(int duration, double distance) {
    this.duration = duration;
    this.distance = distance;
  }

  public RouteEstimateResponseDto() {
  }
}
