package inc.visor.voom_service.ride.dto;

import inc.visor.voom_service.shared.RoutePointDto;

import java.util.List;

public class FavoriteRouteDto {

    private long id;
    private String name;
    private double totalDistanceKm;
    private List<RoutePointDto> points;

    public FavoriteRouteDto() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public List<RoutePointDto> getPoints() {
        return points;
    }

    public void setPoints(List<RoutePointDto> points) {
        this.points = points;
    }

}
