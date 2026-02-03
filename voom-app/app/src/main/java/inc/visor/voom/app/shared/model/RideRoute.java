package inc.visor.voom.app.shared.model;

import java.util.List;

import inc.visor.voom.app.shared.model.enums.RoutePointType;


public class RideRoute {
    public Long id;
    public double totalDistanceKm;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public void setTotalDistanceKm(double totalDistanceKm) {
        this.totalDistanceKm = totalDistanceKm;
    }

    public List<RoutePoint> getRoutePoints() {
        return routePoints;
    }

    public void setRoutePoints(List<RoutePoint> routePoints) {
        this.routePoints = routePoints;
    }

    public List<RoutePoint> routePoints;

    public RoutePoint getPickup() {
        return this.routePoints.stream()
                .filter(rp -> rp.getPointType() == RoutePointType.PICKUP)
                .findFirst()
                .orElse(null);
    }

    public RoutePoint getDropOff() {
        return this.routePoints.stream()
                .filter(rp -> rp.getPointType() == RoutePointType.DROPOFF)
                .findFirst()
                .orElse(null);
    }
}
