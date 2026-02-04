package inc.visor.voom.app.shared.api;

import java.util.List;

import inc.visor.voom.app.shared.dto.vehicle.CreateVehicleDto;
import inc.visor.voom.app.shared.dto.vehicle.VehicleDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface VehicleApi {

    @GET("api/vehicles")
    Call<List<VehicleDto>> getVehicles();

    @GET("api/vehicles/{id}")
    Call<VehicleDto> getVehicle(@Path("id") long id);

    @POST("api/vehicles")
    Call<VehicleDto> createVehicle(@Body CreateVehicleDto dto);

    @PUT("api/vehicles/{id}")
    Call<VehicleDto> updateVehicle(@Path("id") long id, @Body VehicleDto dto);

    @DELETE("api/vehicles/{id}")
    Call<Void> deleteVehicle(@Path("id") long id);

}
