package inc.visor.voom.app.shared.component.history;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.shared.DataStoreManager;
import inc.visor.voom.app.shared.api.RideApi;
import inc.visor.voom.app.shared.dto.RideHistoryDto;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideHistoryViewModel extends ViewModel {

    private final MutableLiveData<List<RideHistoryDto>> _rides = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> _toastMessage = new MutableLiveData<>();
    private final MutableLiveData<String> _startDate = new MutableLiveData<>();
    private final MutableLiveData<String> _endDate = new MutableLiveData<>();
    private final MutableLiveData<String> _column = new MutableLiveData<>();
    private final MutableLiveData<String> _order = new MutableLiveData<>();

    RideApi rideApi;
    DataStoreManager dataStoreManager;
    CompositeDisposable compositeDisposable;
    RideHistoryAdapter adapter;

    public RideHistoryViewModel() {
        rideApi = RetrofitClient.getInstance().create(RideApi.class);
        dataStoreManager = DataStoreManager.getInstance();
        compositeDisposable = new CompositeDisposable();
        _startDate.setValue(null);
        _endDate.setValue(null);
        _column.setValue(null);
        _order.setValue(null);
    }

    public LiveData<List<RideHistoryDto>> getRides() {
        return _rides;
    }

    public LiveData<String> getToastMessage() { return _toastMessage; }

    public MutableLiveData<String> get_order() {
        return _order;
    }

    public MutableLiveData<String> get_column() {
        return _column;
    }

    public MutableLiveData<String> get_endDate() {
        return _endDate;
    }

    public MutableLiveData<String> get_startDate() {
        return _startDate;
    }

    public void loadRideHistory() {
        final String startDate = _startDate.getValue();
        final String endDate = _endDate.getValue();
        final String order = _order.getValue();
        final String column = _column.getValue();

        final Disposable disposable = dataStoreManager.getUserId().subscribe((userId) -> {
            final String userRole = DataStoreManager.getInstance().getUserRole().blockingGet();
            switch (userRole) {
                case "ADMIN":
                    this.rideApi.getRides(startDate, endDate, column, order).enqueue(new Callback<List<RideHistoryDto>>() {
                        @Override
                        public void onResponse(@NonNull Call<List<RideHistoryDto>> call, @NonNull Response<List<RideHistoryDto>> response) {
                            if (response.isSuccessful()) {
                                _rides.setValue(response.body());
                            } else {
                                _rides.setValue(List.of());
                                _toastMessage.setValue("Failed to load rides");
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<List<RideHistoryDto>> call, @NonNull Throwable t) {
                            Log.e("ADMIN_RIDE_HISTORY", "onFailure: " + t);
                            _toastMessage.setValue("Network error");
                        }
                    });
                    break;
                case "USER":
                    this.rideApi.getRidesForUser(userId, startDate, endDate, column, order).enqueue(new Callback<List<RideHistoryDto>>() {
                        @Override
                        public void onResponse(@NonNull Call<List<RideHistoryDto>> call, @NonNull Response<List<RideHistoryDto>> response) {
                            if (response.isSuccessful()) {
                                _rides.setValue(response.body());
                            } else {
                                _rides.setValue(List.of());
                                _toastMessage.setValue("Failed to load rides");
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<List<RideHistoryDto>> call, @NonNull Throwable t) {
                            Log.e("USER_RIDE_HISTORY", "onFailure: " + t);
                            _toastMessage.setValue("Network error");
                        }
                    });
                    break;
                default:
                    break;
            }
        });

        compositeDisposable.add(disposable);
    }

    public void dispose() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            this.compositeDisposable.dispose();
        }
    }
}
