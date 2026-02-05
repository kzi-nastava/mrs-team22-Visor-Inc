package inc.visor.voom.app.unauthenticated.home;

import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inc.visor.voom.app.driver.api.DriverMetaProvider;
import inc.visor.voom.app.driver.dto.DriverSummaryDto;
import inc.visor.voom.app.shared.simulation.DriverSimulationManager;
import inc.visor.voom.app.user.home.model.RoutePoint;

public class UnauthenticatedHomeViewModel extends ViewModel implements DriverMetaProvider {

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

    public void addPoint(RoutePoint point) {
        List<RoutePoint> current = new ArrayList<>(routePoints.getValue());
        current.add(point);
        routePoints.setValue(current);
    }

    public void setActiveDrivers(List<DriverSummaryDto> list) {
        Map<Integer, DriverSummaryDto> map = new HashMap<>();
        if (list != null) {
            for (DriverSummaryDto d : list) map.put(d.id, d);
        }
        activeDriversMap.setValue(map);
    }

    public DriverSummaryDto findActiveDriver(int id) {
        java.util.Map<Integer, DriverSummaryDto> map = activeDriversMap.getValue();
        return map == null ? null : map.get(id);
    }

    public void handleMapClick(double lat, double lng, String adress) {

        List<RoutePoint> current = new ArrayList<>(routePoints.getValue());

        if (current.isEmpty()) {

            current.add(new RoutePoint(
                    lat,
                    lng,
                    adress,
                    0,
                    RoutePoint.PointType.PICKUP
            ));

        } else {
            if (current.size() >= 1) {
                RoutePoint last = current.get(current.size() - 1);
                last.type = RoutePoint.PointType.STOP;
            }

            current.add(new RoutePoint(
                    lat,
                    lng,
                    adress,
                    current.size(),
                    RoutePoint.PointType.DROPOFF
            ));
        }
        reindex(current);
        routePoints.setValue(current);
    }

    private void reindex(List<RoutePoint> list) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).orderIndex = i;

            if (i == 0) {
                list.get(i).type = RoutePoint.PointType.PICKUP;
            } else if (i == list.size() - 1) {
                list.get(i).type = RoutePoint.PointType.DROPOFF;
            } else {
                list.get(i).type = RoutePoint.PointType.STOP;
            }
        }
    }

    public void removePoint(int index) {

        List<RoutePoint> current = new ArrayList<>(routePoints.getValue());

        if (index < 0 || index >= current.size()) return;

        current.remove(index);

        reindex(current);

        routePoints.setValue(current);
    }

    public void setAsDropoff(int index) {

        List<RoutePoint> current = new ArrayList<>(routePoints.getValue());

        if (index <= 0 || index >= current.size()) return;

        RoutePoint selected = current.remove(index);
        current.add(selected);

        reindex(current);

        routePoints.setValue(current);
    }

    public void clearRoute() {
        routePoints.setValue(new ArrayList<>());
    }

}