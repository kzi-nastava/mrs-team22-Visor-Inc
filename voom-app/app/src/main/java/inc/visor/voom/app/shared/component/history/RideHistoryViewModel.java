package inc.visor.voom.app.shared.component.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.shared.DataStoreManager;
import inc.visor.voom.app.shared.api.RideApi;
import inc.visor.voom.app.shared.dto.RideHistoryDto;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class RideHistoryViewModel extends ViewModel {



    private final MutableLiveData<List<RideHistoryDto>> _rides = new MutableLiveData<>(new ArrayList<>());
    RideApi rideApi;
    DataStoreManager dataStoreManager;
    CompositeDisposable compositeDisposable;

    public RideHistoryViewModel() {
        rideApi = RetrofitClient.getInstance().create(RideApi.class);
        dataStoreManager = DataStoreManager.getInstance();
        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<List<RideHistoryDto>> getRides() {
        return _rides;
    }

    public void loadRideHistory() {
        final Disposable disposable = (Disposable) dataStoreManager.getUserId().subscribe((userId) -> {

        });

        compositeDisposable.add(disposable);
    }


}
