package inc.visor.voom.app.driver.api;

import java.util.List;

import inc.visor.voom.app.driver.dto.DriverSummaryDto;
import retrofit2.Call;
import retrofit2.http.GET;

public interface DriverApi {
    @GET("/api/drivers/active")
    Call<List<DriverSummaryDto>> getActiveDrivers();
}
