package inc.visor.voom_service.osrm.dto;

public record RouteRequestDto(
        Long driverId,
        LatLng start,
        LatLng end
) {
}
