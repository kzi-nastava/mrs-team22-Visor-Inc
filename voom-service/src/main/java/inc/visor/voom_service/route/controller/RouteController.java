package inc.visor.voom_service.route.controller;

import inc.visor.voom_service.route.dto.RouteEstimateRequestDto;
import inc.visor.voom_service.route.dto.RouteEstimateResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

  public RouteController() {
  }

  @PostMapping
  public ResponseEntity<RouteEstimateResponseDto> getRouteEstimate(@RequestBody RouteEstimateRequestDto request) {

    RouteEstimateResponseDto response = new RouteEstimateResponseDto();
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

}
