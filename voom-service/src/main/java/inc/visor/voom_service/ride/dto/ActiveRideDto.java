package inc.visor.voom_service.ride.dto;

import java.util.List;

import inc.visor.voom_service.ride.model.enums.RideStatus;
import inc.visor.voom_service.shared.RoutePointDto;

public class ActiveRideDto {
    private Long rideId;
    private RideStatus status;
    private List<RoutePointDto> routePoints;

    public ActiveRideDto() {  }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public List<RoutePointDto> getRoutePoints() {
        return routePoints;
    }

    public void setRoutePoints(List<RoutePointDto> routePoints) {
        this.routePoints = routePoints;
    }
}
