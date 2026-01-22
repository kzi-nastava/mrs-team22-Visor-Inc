package inc.visor.voom.app.shared.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import inc.visor.voom.app.shared.dto.OsrmResponse;
import inc.visor.voom.app.shared.service.OsrmService;
import inc.visor.voom.app.user.home.model.RoutePoint;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RouteRepository {

    private final OsrmService osrmService;

    public RouteRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://router.project-osrm.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        osrmService = retrofit.create(OsrmService.class);
    }

    public void fetchRoute(
            String coords,
            Callback<OsrmResponse> callback
    ) {
        osrmService.getRoute(coords, "full", "geojson")
                .enqueue(callback);
    }

    public void fetchRouteFromPoints(
            List<RoutePoint> points,
            Callback<OsrmResponse> callback
    ) {
        List<RoutePoint> sorted = new ArrayList<>(points);
        Collections.sort(sorted, Comparator.comparingInt(p -> p.orderIndex));

        StringBuilder coords = new StringBuilder();

        for (int i = 0; i < sorted.size(); i++) {
            coords.append(sorted.get(i).lng)
                    .append(",")
                    .append(sorted.get(i).lat);

            if (i < sorted.size() - 1) {
                coords.append(";");
            }
        }

        fetchRoute(coords.toString(), callback);
    }

}

