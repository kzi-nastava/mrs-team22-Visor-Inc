package inc.visor.voom.app.admin.pricing.api;

import java.util.List;

import inc.visor.voom.app.admin.pricing.dto.VehicleTypeDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface VehicleTypeApi {
    @GET("/api/vehicleTypes")
    Call<List<VehicleTypeDto>> getVehicleTypes();
    @PUT("/api/vehicleTypes/{id}")
    Call<VehicleTypeDto> updateVehicleType(@Path("id") long id, @Body VehicleTypeDto body);
}
