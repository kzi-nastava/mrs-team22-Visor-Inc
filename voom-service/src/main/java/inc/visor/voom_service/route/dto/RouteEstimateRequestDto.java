package inc.visor.voom_service.route.dto;

import inc.visor.voom_service.ride.dto.RoutePointDto;

public class RouteEstimateRequestDto {

  private RoutePointDto startPoint;
  private RoutePointDto endPoint;

  public RoutePointDto getStartPoint() {
    return startPoint;
  }

  public void setStartPoint(RoutePointDto startPoint) {
    this.startPoint = startPoint;
  }

  public RoutePointDto getEndPoint() {
    return endPoint;
  }

  public void setEndPoint(RoutePointDto endPoint) {
    this.endPoint = endPoint;
  }
}
