package inc.visor.voom.app.admin.driver_approve.api;

import inc.visor.voom.app.admin.driver_approve.dto.DriverVehicleChangeRequestDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AdminVehicleRequestsApi {

    @GET("/api/admin/vehicle-requests/{id}")
    Call<DriverVehicleChangeRequestDto> getRequest(@Path("id") String id);

    @POST("/api/admin/vehicle-requests/{id}/approve")
    Call<Void> approve(@Path("id") String id);

    @POST("/api/admin/vehicle-requests/{id}/reject")
    Call<Void> reject(@Path("id") String id);
}
