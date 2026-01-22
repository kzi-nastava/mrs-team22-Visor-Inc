package inc.visor.voom.app.user.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import inc.visor.voom.app.user.home.model.RoutePoint;

public class UserHomeViewModel extends ViewModel {

    private final MutableLiveData<List<RoutePoint>> routePoints =
            new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<Boolean> rideLocked =
            new MutableLiveData<>(false);

    public LiveData<List<RoutePoint>> getRoutePoints() {
        return routePoints;
    }

    public LiveData<Boolean> isRideLocked() {
        return rideLocked;
    }

    public void addPoint(RoutePoint point) {
        List<RoutePoint> current = new ArrayList<>(routePoints.getValue());
        current.add(point);
        routePoints.setValue(current);
    }

    public void clearRoute() {
        routePoints.setValue(new ArrayList<>());
    }

    public void lockRide() {
        rideLocked.setValue(true);
    }

    public void unlockRide() {
        rideLocked.setValue(false);
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

}
