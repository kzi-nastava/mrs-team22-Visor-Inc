package inc.visor.voom.app.driver.api;

import inc.visor.voom.app.driver.dto.ActiveRideDto;
import inc.visor.voom.app.driver.dto.StartRideDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RideApi {
    @POST("/api/rides/{rideId}/start")
    Call<Void> startRide(
            @Path("rideId") long rideId,
            @Body StartRideDto dto
    );

    @GET("/api/rides/ongoing")
    Call<ActiveRideDto> getOngoingRide();

}

