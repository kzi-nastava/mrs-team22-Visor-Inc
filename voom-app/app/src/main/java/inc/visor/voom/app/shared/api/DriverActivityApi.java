package inc.visor.voom.app.shared.api;

import inc.visor.voom.app.shared.dto.driver.DriverStateChangeDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DriverActivityApi {

    @POST("/api/activity")
    Call<DriverStateChangeDto> changeDriverState(@Body DriverStateChangeDto dto);

    @GET("/api/activity/{userId}")
    Call<DriverStateChangeDto> getDriverState(@Path("userId") long userId);
}