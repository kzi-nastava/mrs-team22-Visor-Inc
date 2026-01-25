package inc.visor.voom_service.route.controller;

import inc.visor.voom_service.osrm.dto.LatLng;
import inc.visor.voom_service.osrm.service.OsrmService;
import inc.visor.voom_service.ride.service.RideEstimateService;
import inc.visor.voom_service.route.dto.RouteEstimateRequestDto;
import inc.visor.voom_service.route.dto.RouteEstimateResponseDto;
import inc.visor.voom_service.shared.RoutePointDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

  private final RideEstimateService rideEstimateService;
  private final OsrmService osrmService;

  public RouteController(RideEstimateService rideEstimateService, OsrmService osrmService) {
      this.rideEstimateService = rideEstimateService;
      this.osrmService = osrmService;
  }

  @PostMapping
  public ResponseEntity<RouteEstimateResponseDto> getRouteEstimate(@RequestBody RouteEstimateRequestDto dto) {
    final List<LatLng> points = this.osrmService.getRoute(dto.getStartPoint(), dto.getEndPoint());
    final List<RoutePointDto> routePoints = points.stream().map(RoutePointDto::new).toList();
    double distance = this.rideEstimateService.calculateTotalDistanceEstimate(routePoints);
    final RouteEstimateResponseDto routeEstimate = new RouteEstimateResponseDto((int) Math.round(distance * 60), distance);
    return ResponseEntity.status(HttpStatus.CREATED).body(routeEstimate);
  }

}
