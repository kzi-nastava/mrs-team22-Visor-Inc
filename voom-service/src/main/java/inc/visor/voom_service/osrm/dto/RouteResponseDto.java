package inc.visor.voom_service.osrm.dto;

import java.util.List;

public record RouteResponseDto(
    Long driverId,
    List<LatLng> route
) {}