package inc.visor.voom.app.driver.activate.api;

import inc.visor.voom.app.driver.activate.dto.ActivateDriverRequestDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DriverActivationApi {

    @POST("/api/drivers/activation")
    Call<Void> activate(@Body ActivateDriverRequestDto body);
}
