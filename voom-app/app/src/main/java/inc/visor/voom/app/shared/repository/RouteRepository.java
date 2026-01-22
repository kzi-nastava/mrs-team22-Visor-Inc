package inc.visor.voom.app.shared.repository;

import inc.visor.voom.app.shared.dto.OsrmResponse;
import inc.visor.voom.app.shared.service.OsrmService;
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
}

