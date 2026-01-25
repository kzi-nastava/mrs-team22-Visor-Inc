package inc.visor.voom_service.route.dto;

import inc.visor.voom_service.osrm.dto.LatLng;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RouteEstimateRequestDto {

  private LatLng startPoint;
  private LatLng endPoint;

  public RouteEstimateRequestDto() {
  }
}
