package inc.visor.voom.app.user.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class UserHomeViewModel extends ViewModel {

    public enum PointType {
        PICKUP, STOP, DROPOFF
    }

    public static class RoutePoint {
        public double lat;
        public double lng;
        public String address;
        public PointType type;
        public int order;

        public RoutePoint(double lat, double lng, String address, PointType type, int order) {
            this.lat = lat;
            this.lng = lng;
            this.address = address;
            this.type = type;
            this.order = order;
        }
    }

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
}
