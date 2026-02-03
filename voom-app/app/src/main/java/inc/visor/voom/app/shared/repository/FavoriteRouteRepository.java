package inc.visor.voom.app.shared.repository;

import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.shared.api.RideApi;
import inc.visor.voom.app.user.home.dto.CreateFavoriteRouteDto;
import retrofit2.Callback;

public class FavoriteRouteRepository {

    private final RideApi api;

    public FavoriteRouteRepository() {
        api = RetrofitClient
                .getInstance()
                .create(RideApi.class);
    }

    public void createFavoriteRoute(
            CreateFavoriteRouteDto dto,
            Callback<Void> callback
    ) {
        api.createFavoriteRoute(dto).enqueue(callback);
    }
}
