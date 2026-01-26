package inc.visor.voom.app.shared.api;

import inc.visor.voom.app.user.home.dto.RideRequestDto;
import inc.visor.voom.app.user.home.dto.RideRequestResponseDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RideApi {

    @POST("/api/rides/requests")
    Call<RideRequestResponseDto> createRideRequest(
            @Body RideRequestDto payload
    );
}

