package inc.visor.voom_service.ride.model;

import inc.visor.voom_service.ride.model.enums.RoutePointType;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "favorite_route")
public class FavoriteRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_route_id", nullable = false)
    private long id;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "total_distance_km", nullable = false)
    private double totalDistanceKm;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "favorite_route_id")
    @OrderBy("orderIndex ASC")
    private List<RoutePoint> routePoints;

    public FavoriteRoute() {
    }

    // getters / setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
