package inc.visor.voom_service.route.controller;

import inc.visor.voom_service.osrm.dto.LatLng;
import inc.visor.voom_service.osrm.service.OsrmService;
import inc.visor.voom_service.ride.service.RideEstimateService;
import inc.visor.voom_service.route.dto.RouteEstimateRequestDto;
import inc.visor.voom_service.route.dto.RouteEstimateResponseDto;
import inc.visor.voom_service.shared.RoutePointDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

  private final RideEstimateService rideEstimateService;
  private final OsrmService osrmService;
  Logger logger = LoggerFactory.getLogger(RouteController.class);

  public RouteController(RideEstimateService rideEstimateService, OsrmService osrmService) {
      this.rideEstimateService = rideEstimateService;
      this.osrmService = osrmService;
  }

  @PostMapping
  public ResponseEntity<RouteEstimateResponseDto> getRouteEstimate(@Valid @RequestBody RouteEstimateRequestDto dto) {
    final List<LatLng> points = this.osrmService.getRoute(dto.getRoutePoints().stream().map(point -> new LatLng(point.getLat(), point.getLng())).toList());
    logger.info("getRouteEstimate: routePoints = {}", points);
    final List<RoutePointDto> routePoints = points.stream().map(point -> new RoutePointDto(point, points.indexOf(point))).toList();
    double distance = this.rideEstimateService.calculateTotalDistanceEstimate(routePoints);
    final RouteEstimateResponseDto routeEstimate = new RouteEstimateResponseDto((int) Math.round(distance * 2.5), distance);
    return ResponseEntity.status(HttpStatus.CREATED).body(routeEstimate);
  }

}
