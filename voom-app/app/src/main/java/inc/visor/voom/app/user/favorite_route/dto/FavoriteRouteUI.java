package inc.visor.voom.app.user.favorite_route.dto;

import java.util.List;

public class FavoriteRouteUI {

    public FavoriteRouteDto dto;

    public long id;
    public String name;
    public String start;
    public String end;
    public double distanceKm;
    public List<String> stops;

    public FavoriteRouteUI(
            FavoriteRouteDto dto,
            long id,
            String name,
            String start,
            String end,
            double distanceKm,
            List<String> stops
    ) {
        this.dto = dto;
        this.id = id;
        this.name = name;
        this.start = start;
        this.end = end;
        this.distanceKm = distanceKm;
        this.stops = stops;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FavoriteRouteUI)) return false;

        FavoriteRouteUI that = (FavoriteRouteUI) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}

