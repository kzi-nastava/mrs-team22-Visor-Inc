package inc.visor.voom.app.shared.api;

import java.util.List;

import inc.visor.voom.app.driver.dto.ActiveRideDto;
import inc.visor.voom.app.shared.dto.RideHistoryDto;
import inc.visor.voom.app.shared.dto.StartScheduledRideDto;
import inc.visor.voom.app.user.favorite_route.dto.FavoriteRouteDto;
import inc.visor.voom.app.user.home.dto.CreateFavoriteRouteDto;
import inc.visor.voom.app.user.home.dto.RideRequestDto;
import inc.visor.voom.app.user.home.dto.RideRequestResponseDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Path;

public interface RideApi {

    @POST("/api/rides/requests")
    Call<RideRequestResponseDto> createRideRequest(
            @Body RideRequestDto payload
    );

    @GET("/api/rides/ongoing")
    Call<ActiveRideDto> getOngoingRide();

    @GET("/api/rides/driver/history")
    Call<List<RideHistoryDto>> getDriverRideHistory(@Query("dateFrom") String dateFrom,
                                                    @Query("dateTo") String dateTo,
                                                    @Query("sort") String sort);
    @POST("/api/rides/favorites")
    Call<Void> createFavoriteRoute(@Body CreateFavoriteRouteDto dto);

    @GET("/api/rides/favorites")
    Call<List<FavoriteRouteDto>> getFavoriteRoutes();

    @DELETE("/api/rides/favorites/{id}")
    Call<Void> deleteFavoriteRoute(@Path("id") long routeId);

    @POST("/api/rides/scheduled/{rideId}")
    Call<Void> startScheduleRide(
            @Path("rideId") long rideId,
            @Body StartScheduledRideDto payload
    );


}

