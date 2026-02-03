package inc.visor.voom.app.user.favorite_route;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.shared.api.RideApi;
import inc.visor.voom.app.shared.dto.RoutePointDto;
import inc.visor.voom.app.user.favorite_route.dto.FavoriteRouteDto;
import inc.visor.voom.app.user.favorite_route.dto.FavoriteRouteUI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteRoutesViewModel extends ViewModel {

    private final MutableLiveData<List<FavoriteRouteUI>> routes =
            new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<Boolean> loading =
            new MutableLiveData<>(false);

    private final RideApi api =
            RetrofitClient.getInstance().create(RideApi.class);

    public LiveData<List<FavoriteRouteUI>> getRoutes() {
        return routes;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public void fetch() {

        loading.setValue(true);

        api.getFavoriteRoutes().enqueue(new Callback<List<FavoriteRouteDto>>() {
            @Override
            public void onResponse(Call<List<FavoriteRouteDto>> call,
                                   Response<List<FavoriteRouteDto>> response) {

                loading.setValue(false);

                if (!response.isSuccessful() || response.body() == null) {
                    routes.setValue(new ArrayList<>());
                    return;
                }

                List<FavoriteRouteUI> mapped = new ArrayList<>();

                for (FavoriteRouteDto dto : response.body()) {
                    mapped.add(mapDto(dto));
                }

                routes.setValue(mapped);
            }

            @Override
            public void onFailure(Call<List<FavoriteRouteDto>> call, Throwable t) {
                loading.setValue(false);
            }
        });
    }

    private FavoriteRouteUI mapDto(FavoriteRouteDto dto) {

        if (dto.points == null) {
            return new FavoriteRouteUI(
                    dto,
                    dto.id,
                    dto.name,
                    "",
                    "",
                    dto.totalDistanceKm,
                    new ArrayList<>()
            );
        }

        dto.points.sort(
                Comparator.comparingInt(p -> p.orderIndex != null ? p.orderIndex : 0)
        );


        RoutePointDto pickup = null;
        RoutePointDto dropoff = null;

        for (RoutePointDto p : dto.points) {
            if ("PICKUP".equals(p.type)) pickup = p;
            if ("DROPOFF".equals(p.type)) dropoff = p;
        }

        List<String> stops = new ArrayList<>();

        for (RoutePointDto p : dto.points) {
            if ("STOP".equals(p.type)) {
                stops.add(shortAddress(p.address));
            }
        }

        return new FavoriteRouteUI(
                dto,
                dto.id,
                dto.name,
                shortAddress(pickup != null ? pickup.address : ""),
                shortAddress(dropoff != null ? dropoff.address : ""),
                dto.totalDistanceKm,
                stops
        );
    }

    private String shortAddress(String address) {

        if (address == null) return "";

        String[] parts = address.split(",");

        if (parts.length >= 2) {
            return parts[0].trim() + ", " + parts[1].trim();
        }

        return address;
    }
}
