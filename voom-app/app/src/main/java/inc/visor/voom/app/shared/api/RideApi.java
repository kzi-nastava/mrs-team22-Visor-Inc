package inc.visor.voom.app.shared.api;

import java.util.List;

import inc.visor.voom.app.driver.dto.ActiveRideDto;
import inc.visor.voom.app.driver.dto.StartRideDto;
import inc.visor.voom.app.shared.dto.RideHistoryDto;
import inc.visor.voom.app.shared.dto.StartScheduledRideDto;
import inc.visor.voom.app.shared.dto.ride.RideCancellationDto;
import inc.visor.voom.app.shared.dto.ride.RideResponseDto;
import inc.visor.voom.app.shared.dto.ride.RideStopDto;
import inc.visor.voom.app.shared.dto.route.RouteEstimateRequestDto;
import inc.visor.voom.app.shared.dto.route.RouteEstimateResponseDto;
import inc.visor.voom.app.user.favorite_route.dto.FavoriteRouteDto;
import inc.visor.voom.app.user.home.dto.CreateFavoriteRouteDto;
import inc.visor.voom.app.user.home.dto.RideRequestDto;
import inc.visor.voom.app.user.home.dto.RideRequestResponseDto;
import inc.visor.voom.app.user.tracking.dto.ComplaintRequestDto;
import inc.visor.voom.app.user.tracking.dto.RatingRequestDto;
import inc.visor.voom.app.user.tracking.dto.RidePanicDto;
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

    @POST("/api/routes")
    Call<RouteEstimateResponseDto> getRouteEstimate(@Body RouteEstimateRequestDto payload);


    @POST("/api/rating/{rideId}")
    Call<Void> rateRide(@Path("rideId") Long rideId, @Body RatingRequestDto body);

    @POST("/api/complaints/ride/{rideId}")
    Call<Void> reportRide(@Path("rideId") Long rideId, @Body ComplaintRequestDto body);

    @POST("/api/rides/{rideId}/panic")
    Call<Void> panic(@Path("rideId") Long rideId, @Body RidePanicDto body);

    @POST("/api/rides/{rideId}/cancel")
    Call<Void> cancel(@Path("rideId") Long rideId, @Body RideCancellationDto body);
    @POST("/api/rides/finish-ongoing")
    Call<Void> finishOngoing();

    @GET("/api/rides/ongoing/driver/{driverId}")
    Call<ActiveRideDto> getOngoingByDriverId(@Path("driverId") long driverId);

    @POST("/api/rides/{rideId}/start")
    Call<Void> startRide(
            @Path("rideId") long rideId,
            @Body StartRideDto dto
    );

    @POST("/api/rides/{id}/stop")
    Call<RideResponseDto> stopRide(@Path("id") Long id, @Body RideStopDto dto);
}

