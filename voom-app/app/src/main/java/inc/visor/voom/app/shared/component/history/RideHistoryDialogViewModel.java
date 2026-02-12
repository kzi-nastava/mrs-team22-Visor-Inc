package inc.visor.voom.app.shared.component.history;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import inc.visor.voom.app.shared.model.RoutePoint;

public class RideHistoryDialogViewModel {
    public enum ScheduleType { NOW, LATER }
    private final MutableLiveData<String> scheduledTime = new MutableLiveData<>(null); // "HH:mm"
    private final MutableLiveData<Boolean> scheduledTimeValid = new MutableLiveData<>(true);
    private final MutableLiveData<List<RoutePoint>> routePoints = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<String> getScheduledTime() {
        return scheduledTime;
    }

    public MutableLiveData<Boolean> getScheduledTimeValid() {
        return scheduledTimeValid;
    }

    public MutableLiveData<List<RoutePoint>> getRoutePoints() {
        return routePoints;
    }
}
