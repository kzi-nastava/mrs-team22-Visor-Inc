package inc.visor.voom.app.driver.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inc.visor.voom.app.driver.api.DriverMetaProvider;
import inc.visor.voom.app.driver.dto.DriverAssignedDto;
import inc.visor.voom.app.driver.dto.DriverSummaryDto;
import inc.visor.voom.app.shared.service.DriverAssignmentListener;
import inc.visor.voom.app.shared.service.DriverSimulationWsService;
import inc.visor.voom.app.shared.simulation.DriverSimulationManager;

public class DriverHomeViewModel extends ViewModel implements DriverMetaProvider, DriverAssignmentListener {

    private final DriverSimulationManager simulationManager = new DriverSimulationManager();
    private DriverSimulationWsService wsService;

    private final MutableLiveData<DriverAssignedDto> assignedRide =
            new MutableLiveData<>();

    private final MutableLiveData<Map<Integer, DriverSummaryDto>> activeDriversMap =
            new MutableLiveData<>(new HashMap<>());

    public LiveData<Map<Integer, DriverSummaryDto>> getActiveDriversMap() {
        return activeDriversMap;
    }
    private final MutableLiveData<Long> myDriverId = new MutableLiveData<>(null);

    private boolean started = false;

    public LiveData<Long> getMyDriverId() {
        return myDriverId;
    }

    public void setMyDriverId(Long id) {
        myDriverId.setValue(id);
    }

    public LiveData<DriverAssignedDto> getAssignedRide() {
        return assignedRide;
    }

    public void onDriverAssigned(DriverAssignedDto dto) {

        Long myId = myDriverId.getValue();
        if (myId == null) return;

        if (dto.driverId == myId) {
            Log.d("RIDE_ID_DEBUG","THIS: " + dto.rideId);
            assignedRide.postValue(dto);
        }
    }
    public DriverSimulationManager getSimulationManager() {
        return simulationManager;
    }

    public void startSimulation() {

        if (wsService != null) return;

        simulationManager.startInterpolationLoop();

        wsService = new DriverSimulationWsService(
                simulationManager,
                this,
                this
        );

        wsService.connect();
    }


    public void setActiveDrivers(List<DriverSummaryDto> list) {
        Map<Integer, DriverSummaryDto> map = new HashMap<>();
        if (list != null) {
            for (DriverSummaryDto d : list) {
                map.put(d.id, d);
            }
        }
        activeDriversMap.setValue(map);
    }

    public DriverSummaryDto findActiveDriver(int id) {
        Map<Integer, DriverSummaryDto> map = activeDriversMap.getValue();
        return map == null ? null : map.get(id);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        if (wsService != null) wsService.disconnect();
    }
}
