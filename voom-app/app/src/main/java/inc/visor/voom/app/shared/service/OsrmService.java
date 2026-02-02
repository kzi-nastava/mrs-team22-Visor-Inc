package inc.visor.voom.app.shared.service;

import inc.visor.voom.app.shared.dto.OsrmResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OsrmService {

    @GET("route/v1/driving/{coords}")
    Call<OsrmResponse> getRoute(
            @Path("coords") String coords,
            @Query("overview") String overview,
            @Query("geometries") String geometries
    );
}
