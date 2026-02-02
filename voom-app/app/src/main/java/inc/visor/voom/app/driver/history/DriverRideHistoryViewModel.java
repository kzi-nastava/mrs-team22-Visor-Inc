package inc.visor.voom.app.driver.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import inc.visor.voom.app.driver.history.models.Passenger;
import inc.visor.voom.app.driver.history.models.Ride;
import inc.visor.voom.app.driver.history.models.RideHistoryModels.*;
import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.shared.api.RideApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverRideHistoryViewModel extends ViewModel {

    private final MutableLiveData<List<RideHistoryDto>> rides = new MutableLiveData<>();

    public LiveData<List<RideHistoryDto>> getRides() { return rides; }

    public void fetchHistory(Date start, Date end, boolean ascending) {
        RideApi api = RetrofitClient.getInstance().create(RideApi.class);

        String from = (start == null) ? null : formatToIso(start, true);
        String to = (end == null) ? null : formatToIso(end, false);
        String sortDir = ascending ? "ASC" : "DESC";

        api.getDriverRideHistory(from, to, sortDir).enqueue(new Callback<List<RideHistoryDto>>() {
            @Override
            public void onResponse(Call<List<RideHistoryDto>> call, Response<List<RideHistoryDto>> response) {
                if (response.isSuccessful()) {
                    rides.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<RideHistoryDto>> call, Throwable t) {
            }
        });
    }

    private String formatToIso(Date date, boolean isStart) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (isStart) {
            cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0);
        } else {
            cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59);
        }
        SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        return iso.format(cal.getTime());
    }


    public void clearFilters(boolean asc) {
        fetchHistory(null, null, asc);
    }
}
