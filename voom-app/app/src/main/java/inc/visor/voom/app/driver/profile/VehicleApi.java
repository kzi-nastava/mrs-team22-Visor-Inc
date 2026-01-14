package inc.visor.voom.app.driver.profile;

import inc.visor.voom.app.driver.profile.dto.VehicleSummaryDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface VehicleApi {

    @GET("/api/drivers/me")
    Call<VehicleSummaryDto> getMyVehicle();

    @PUT("/api/drivers/me")
    Call<VehicleSummaryDto> updateMyVehicle(
            @Body VehicleSummaryDto body
    );
}
