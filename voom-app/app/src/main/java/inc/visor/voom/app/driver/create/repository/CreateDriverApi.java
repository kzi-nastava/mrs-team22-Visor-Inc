package inc.visor.voom.app.driver.create.repository;

import inc.visor.voom.app.driver.create.dto.CreateDriverRequestDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CreateDriverApi {

    @POST("/api/drivers")
    Call<Void> createDriver(
            @Body CreateDriverRequestDto body
    );
}
