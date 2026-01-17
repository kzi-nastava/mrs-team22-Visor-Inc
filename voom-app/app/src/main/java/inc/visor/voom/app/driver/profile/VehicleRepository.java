package inc.visor.voom.app.driver.profile;

import android.util.Log;

import inc.visor.voom.app.driver.profile.dto.VehicleSummaryDto;
import inc.visor.voom.app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleRepository {

    private final VehicleApi api;

    public VehicleRepository() {
        api = RetrofitClient
                .getInstance()
                .create(VehicleApi.class);
    }

    public void getVehicle(CallbackApi callback) {

        api.getMyVehicle().enqueue(new Callback<>() {
            @Override
            public void onResponse(
                    Call<VehicleSummaryDto> call,
                    Response<VehicleSummaryDto> response
            ) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to load vehicle");
                }
            }

            @Override
            public void onFailure(Call<VehicleSummaryDto> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void updateVehicle(
            VehicleSummaryDto body,
            CallbackApi callback
    ) {

        api.updateMyVehicle(body).enqueue(new Callback<>() {
            @Override
            public void onResponse(
                    Call<VehicleSummaryDto> call,
                    Response<VehicleSummaryDto> response
            ) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Vehicle update failed");
                }
            }

            @Override
            public void onFailure(Call<VehicleSummaryDto> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public interface CallbackApi {
        void onSuccess(VehicleSummaryDto dto);
        void onError(String error);
    }
}
