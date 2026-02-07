package inc.visor.voom.app.admin.tracking;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import inc.visor.voom.app.admin.pricing.dto.VehicleTypeDto;
import inc.visor.voom.app.driver.api.DriverApi;
import inc.visor.voom.app.driver.api.DriverMetaProvider;
import inc.visor.voom.app.driver.dto.ActiveRideDto;
import inc.visor.voom.app.driver.dto.DriverSummaryDto;
import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.shared.api.RideApi;
import inc.visor.voom.app.shared.simulation.DriverSimulationManager;
import inc.visor.voom.app.user.home.model.RoutePoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminTrackingViewModel extends ViewModel implements DriverMetaProvider {
    // Replicates your Signals
    private final MutableLiveData<List<DriverSummaryDto>> _allDrivers = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> _searchTerm = new MutableLiveData<>("");
    private final MutableLiveData<Integer> _selectedDriverId = new MutableLiveData<>(-1);

    // Replicates filteredDrivers = computed(...)
    public final MediatorLiveData<List<DriverSummaryDto>> filteredDrivers = new MediatorLiveData<>();

    // Replicates activeRide = toSignal(...)
    private final MutableLiveData<ActiveRideDto> _activeRide = new MutableLiveData<>();
    public LiveData<ActiveRideDto> activeRide = _activeRide;

    private final DriverSimulationManager simulationManager = new DriverSimulationManager();
    public DriverSimulationManager getSimulationManager() {
        return simulationManager;
    }

    private final MutableLiveData<List<RoutePoint>> routePoints =
            new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<RoutePoint>> getRoutePoints() {
        return routePoints;
    }
    private final MutableLiveData<Map<Integer, DriverSummaryDto>> activeDriversMap =
            new MutableLiveData<>(new HashMap<>());

    public AdminTrackingViewModel() {
        // Combine allDrivers and searchTerm to create the filtered list
        filteredDrivers.addSource(_allDrivers, drivers -> filter());
        filteredDrivers.addSource(_searchTerm, term -> filter());
    }

    private void filter() {
        List<DriverSummaryDto> drivers = _allDrivers.getValue();
        String term = _searchTerm.getValue().toLowerCase().trim();

        if (drivers == null) return;
        if (term.isEmpty()) {
            filteredDrivers.setValue(drivers);
            return;
        }

        List<DriverSummaryDto> filtered = drivers.stream()
                .filter(d -> d.firstName.toLowerCase().contains(term) || d.lastName.toLowerCase().contains(term))
                .collect(Collectors.toList());
        filteredDrivers.setValue(filtered);
    }

    public void setSelectedDriverId(int id) {
        _selectedDriverId.setValue(id);
        fetchActiveRide(id);
    }

    private void fetchActiveRide(int driverId) {

        RideApi rideApi = RetrofitClient.getInstance().create(RideApi.class);

        rideApi.getOngoingByDriverId(driverId).enqueue(new Callback<ActiveRideDto>() {
            @Override
            public void onResponse(Call<ActiveRideDto> call, Response<ActiveRideDto> response) {
                Log.d("FETCHING RIDE", "Driver id: " + driverId);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("FETCHED RIDE", "ride id: " + response.body().rideId);
                    _activeRide.setValue(response.body());
//                    routePoints.setValue(_activeRide.getValue().getRoutePoints().str);
                } else {
                    _activeRide.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ActiveRideDto> call, Throwable t) {

            }
        });

    }

    public void fetchActiveDrivers() {
        DriverApi driverApi = RetrofitClient.getInstance().create(DriverApi.class);

        driverApi.getActiveDrivers().enqueue(new Callback<List<DriverSummaryDto>>() {
            @Override
            public void onResponse(Call<List<DriverSummaryDto>> call, Response<List<DriverSummaryDto>> response) {
                Log.d("VoomAPI", "Drivers found: " + response.body().size());
                if (response.isSuccessful()) {
                    setAllDrivers(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<DriverSummaryDto>> call, Throwable t) {
                Log.d("VoomAPI", "cant get drivers");
            }
        });
    }

    public void setAllDrivers(List<DriverSummaryDto> drivers) { _allDrivers.setValue(drivers); }

    public void setSearchTerm(String term) { _searchTerm.setValue(term); }

    public long getSelectedDriverId() {
        if (_selectedDriverId.getValue() != null) {
            return _selectedDriverId.getValue();
        }
        return -1;
    }

    @Override
    public DriverSummaryDto findActiveDriver(int id) {
        return _allDrivers.getValue().stream().filter(d -> d.getId() == id).findFirst().orElse(null);
    }
}