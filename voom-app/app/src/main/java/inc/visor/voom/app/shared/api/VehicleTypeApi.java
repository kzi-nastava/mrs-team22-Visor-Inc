package inc.visor.voom.app.shared.api;

import java.util.List;

import inc.visor.voom.app.shared.dto.vehicle.CreateVehicleTypeDto;
import inc.visor.voom.app.shared.dto.vehicle.VehicleTypeDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface VehicleTypeApi {

    @GET("api/vehicleTypes")
    Call<List<VehicleTypeDto>> getVehicleTypes();

    @GET("api/vehicleTypes/{id}")
    Call<VehicleTypeDto> getVehicleType(@Path("id") long id);

    @POST("api/vehicleTypes")
    Call<VehicleTypeDto> createVehicleType(@Body CreateVehicleTypeDto dto);

    @PUT("api/vehicleTypes/{id}")
    Call<VehicleTypeDto> updateVehicleType(@Path("id") long id, @Body VehicleTypeDto dto);

    @DELETE("api/vehicleTypes/{id}")
    Call<Void> deleteVehicleType(@Path("id") long id);

}
