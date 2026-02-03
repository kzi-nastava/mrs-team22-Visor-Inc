package inc.visor.voom.app.shared.api;

import java.util.List;

import inc.visor.voom.app.driver.dto.ActiveRideDto;
import inc.visor.voom.app.shared.dto.RideHistoryDto;
import inc.visor.voom.app.user.home.dto.RideRequestDto;
import inc.visor.voom.app.user.home.dto.RideRequestResponseDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

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

}

