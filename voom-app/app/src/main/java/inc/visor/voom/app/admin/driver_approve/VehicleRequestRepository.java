package inc.visor.voom.app.admin.driver_approve;


import inc.visor.voom.app.admin.driver_approve.api.AdminVehicleRequestsApi;
import inc.visor.voom.app.admin.driver_approve.dto.DriverVehicleChangeRequestDto;
import inc.visor.voom.app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleRequestRepository {

    private final AdminVehicleRequestsApi api =
            RetrofitClient.getInstance().create(AdminVehicleRequestsApi.class);

    public void getRequest(String id, Callback<DriverVehicleChangeRequestDto> callback) {
        api.getRequest(id).enqueue(callback);
    }

    public void approve(String id, Callback<Void> callback) {
        api.approve(id).enqueue(callback);
    }

    public void reject(String id, Callback<Void> callback) {
        api.reject(id).enqueue(callback);
    }
}
