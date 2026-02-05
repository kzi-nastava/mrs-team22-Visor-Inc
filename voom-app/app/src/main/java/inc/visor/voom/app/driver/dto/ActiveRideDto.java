package inc.visor.voom.app.driver.dto;


import java.util.List;

import inc.visor.voom.app.shared.dto.RoutePointDto;

public class ActiveRideDto {
    public Long rideId;
    public String status;
    public List<RoutePointDto> routePoints;
    public Long driverId;

    public String toString() {
        return this.routePoints.toString();
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<RoutePointDto> getRoutePoints() {
        return routePoints;
    }

    public void setRoutePoints(List<RoutePointDto> routePoints) {
        this.routePoints = routePoints;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }
}
