package inc.visor.voom.app.shared.component.scheduled;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.shared.DataStoreManager;
import inc.visor.voom.app.shared.api.RideApi;
import inc.visor.voom.app.shared.dto.RideHistoryDto;
import inc.visor.voom.app.shared.dto.ride.RideCancellationDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduledRidesViewModel extends ViewModel {

    private final MutableLiveData<List<RideHistoryDto>> _rides = new MutableLiveData<>(new ArrayList<>());
    RideApi rideApi = RetrofitClient.getInstance().create(RideApi.class);


    public LiveData<List<RideHistoryDto>> getRides() {
        return _rides;
    }

    private final MutableLiveData<String> _toastMessage = new MutableLiveData<>();
    public LiveData<String> getToastMessage() { return _toastMessage; }

    public void loadScheduledRides() {

        boolean isDriver = DataStoreManager.getInstance().getUserRole().blockingGet().equals("DRIVER");


        if (isDriver) {
            rideApi.getScheduledRidesDriver().enqueue(new Callback<List<RideHistoryDto>>() {
                @Override
                public void onResponse(Call<List<RideHistoryDto>> call, Response<List<RideHistoryDto>> response) {
                    if (response.isSuccessful()) {
                        _rides.setValue(response.body());
                    } else {
                        _rides.setValue(List.of());
                        _toastMessage.setValue("Failed to load rides");
                    }
                }

                @Override
                public void onFailure(Call<List<RideHistoryDto>> call, Throwable t) {
                    _toastMessage.setValue("Network error");
                }
            });
        } else {
            Long currentUserId = DataStoreManager.getInstance().getUserId().blockingGet();
            rideApi.getScheduledRides(currentUserId).enqueue(new Callback<List<RideHistoryDto>>() {
                @Override
                public void onResponse(Call<List<RideHistoryDto>> call, Response<List<RideHistoryDto>> response) {
                    if (response.isSuccessful()) {
                        _rides.setValue(response.body());
                    } else {
                        _rides.setValue(List.of());
                        _toastMessage.setValue("Failed to load rides");
                    }
                }

                @Override
                public void onFailure(Call<List<RideHistoryDto>> call, Throwable t) {
                    _toastMessage.setValue("Network error");
                }
            });
        }
    }

    public void cancelRide(RideHistoryDto ride) {

        long rideId = ride.getId();
        long userId = DataStoreManager.getInstance().getUserId().blockingGet();
        RideCancellationDto dto = new RideCancellationDto();
        dto.setMessage("user cancelation reason");
        dto.setUserId(userId);

        RideApi rideApi = RetrofitClient.getInstance().create(RideApi.class);

        rideApi.cancelScheduledRide(rideId, dto).enqueue(new Callback<RideHistoryDto>() {
            @Override
            public void onResponse(Call<RideHistoryDto> call, Response<RideHistoryDto> response) {
                if (response.isSuccessful()) {
                    _toastMessage.setValue("Ride cancelled successfully");

                    loadScheduledRides();
                } else {
                    _toastMessage.setValue("You can't cancel the ride");
                }
            }

            @Override
            public void onFailure(Call<RideHistoryDto> call, Throwable t) {
                _toastMessage.setValue("Network error");
            }
        });
    }
}