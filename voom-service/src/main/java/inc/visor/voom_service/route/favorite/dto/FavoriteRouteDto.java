package inc.visor.voom_service.route.favorite.dto;

import inc.visor.voom_service.ride.model.RideRoute;

public class FavoriteRouteDto {

    private long id;
    private String name;
    private RideRoute route;

    public FavoriteRouteDto() {
    }

    public FavoriteRouteDto(long id, String name, RideRoute route) {
        this.id = id;
        this.name = name;
        this.route = route;
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

    public RideRoute getRoute() {
        return route;
    }

    public void setRoute(RideRoute route) {
        this.route = route;
    }
    
}
