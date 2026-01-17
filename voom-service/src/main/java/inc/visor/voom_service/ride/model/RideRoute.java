package inc.visor.voom_service.ride.model;

import java.util.List;

import inc.visor.voom_service.ride.model.enums.RoutePointType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "ride_route")
public class RideRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ride_route_id", nullable = false)
    private long id;

    @Column(name = "total_distance_km", nullable = false)
    private double totalDistanceKm;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ride_route_id")
    @OrderBy("orderIndex ASC")
    private List<RoutePoint> routePoints;

    public RideRoute() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public RoutePoint getPickupPoint() {
        return routePoints.stream()
                .filter(rp -> rp.getPointType() == RoutePointType.PICKUP)
                .findFirst()
                .orElse(null);
    }

    public RoutePoint getDropoffPoint() {
        return routePoints.stream()
                .filter(rp -> rp.getPointType() == RoutePointType.DROPOFF)
                .findFirst()
                .orElse(null);
    }
    
}
