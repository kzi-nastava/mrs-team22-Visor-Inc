package inc.visor.voom.app.admin.driver_approve;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import inc.visor.voom.app.admin.driver_approve.dto.DriverVehicleChangeRequestDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleApproveViewModel extends ViewModel {

    private final VehicleRequestRepository repository = new VehicleRequestRepository();

    private final MutableLiveData<DriverVehicleChangeRequestDto> request = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> message = new MutableLiveData<>();

    public void loadRequest(String id) {
        loading.setValue(true);

        repository.getRequest(id, new Callback<DriverVehicleChangeRequestDto>() {
            @Override
            public void onResponse(Call<DriverVehicleChangeRequestDto> call,
                                   Response<DriverVehicleChangeRequestDto> response) {
                loading.setValue(false);
                if (response.isSuccessful()) {
                    request.setValue(response.body());
                } else {
                    message.setValue("Request not found");
                }
            }

            @Override
            public void onFailure(Call<DriverVehicleChangeRequestDto> call, Throwable t) {
                loading.setValue(false);
                message.setValue("Network error");
            }
        });
    }

    public void approve(String id) {
        repository.approve(id, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                message.setValue("Request approved");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                message.setValue("Approval failed");
            }
        });
    }

    public void reject(String id) {
        repository.reject(id, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                message.setValue("Request rejected");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                message.setValue("Rejection failed");
            }
        });
    }

    public LiveData<DriverVehicleChangeRequestDto> getRequest() { return request; }
    public LiveData<Boolean> isLoading() { return loading; }
    public LiveData<String> getMessage() { return message; }
}
