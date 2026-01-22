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

    public enum ScheduleType { NOW, LATER }

    private final MutableLiveData<Integer> selectedVehicleId = new MutableLiveData<>(null);
    private final MutableLiveData<ScheduleType> selectedScheduleType = new MutableLiveData<>(ScheduleType.NOW);
    private final MutableLiveData<String> scheduledTime = new MutableLiveData<>(null); // "HH:mm"
    private final MutableLiveData<List<String>> passengerEmails = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> scheduledTimeValid = new MutableLiveData<>(true);

    public LiveData<Integer> getSelectedVehicleId() { return selectedVehicleId; }
    public LiveData<ScheduleType> getSelectedScheduleType() { return selectedScheduleType; }
    public LiveData<String> getScheduledTime() { return scheduledTime; }
    public LiveData<List<String>> getPassengerEmails() { return passengerEmails; }
    public LiveData<Boolean> isScheduledTimeValid() { return scheduledTimeValid; }

    public void setVehicle(Integer id) { selectedVehicleId.setValue(id); }

    public void setScheduleType(ScheduleType t) {
        selectedScheduleType.setValue(t);
        if (t == ScheduleType.NOW) {
            scheduledTime.setValue(null);
            scheduledTimeValid.setValue(true);
        }
    }

    public void setScheduledTime(String hhmm) {
        scheduledTime.setValue(hhmm);
        scheduledTimeValid.setValue(isWithinNext5Hours(hhmm));
    }

    public boolean addPassengerEmail(String email) {
        email = email == null ? "" : email.trim();
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) return false;

        List<String> cur = new ArrayList<>(passengerEmails.getValue());
        if (cur.size() >= 3) return false;
        if (cur.contains(email)) return false;

        cur.add(email);
        passengerEmails.setValue(cur);
        return true;
    }

    public void removePassengerEmail(String email) {
        List<String> cur = new ArrayList<>(passengerEmails.getValue());
        cur.remove(email);
        passengerEmails.setValue(cur);
    }

    private boolean isWithinNext5Hours(String hhmm) {
        if (hhmm == null) return false;
        String[] parts = hhmm.split(":");
        if (parts.length != 2) return false;

        int h = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);

        java.util.Calendar now = java.util.Calendar.getInstance();
        java.util.Calendar selected = (java.util.Calendar) now.clone();
        selected.set(java.util.Calendar.HOUR_OF_DAY, h);
        selected.set(java.util.Calendar.MINUTE, m);
        selected.set(java.util.Calendar.SECOND, 0);

        long diffMs = selected.getTimeInMillis() - now.getTimeInMillis();
        long diffMin = diffMs / 60000;

        return diffMin >= 0 && diffMin <= 300;
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
