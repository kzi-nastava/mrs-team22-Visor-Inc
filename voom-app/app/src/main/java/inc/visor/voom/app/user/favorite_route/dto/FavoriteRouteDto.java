package inc.visor.voom.app.user.favorite_route.dto;

import java.io.Serializable;
import java.util.List;

import inc.visor.voom.app.shared.dto.RoutePointDto;

public class FavoriteRouteDto implements Serializable {
    public long id;
    public String name;
    public double totalDistanceKm;
    public List<RoutePointDto> points;
}
