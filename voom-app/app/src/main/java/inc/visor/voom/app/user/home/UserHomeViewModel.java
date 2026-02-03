package inc.visor.voom.app.user.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inc.visor.voom.app.driver.api.DriverMetaProvider;
import inc.visor.voom.app.driver.dto.DriverSummaryDto;
import inc.visor.voom.app.shared.dto.RoutePointDto;
import inc.visor.voom.app.shared.simulation.DriverSimulationManager;
import inc.visor.voom.app.user.home.dto.CreateFavoriteRouteDto;
import inc.visor.voom.app.user.home.dto.RideRequestDto;
import inc.visor.voom.app.user.home.model.RoutePoint;

public class UserHomeViewModel extends ViewModel implements DriverMetaProvider {

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

    private final MutableLiveData<Map<Integer, DriverSummaryDto>> activeDriversMap =
            new MutableLiveData<>(new HashMap<>());

    public void setVehicle(Integer id) { selectedVehicleId.setValue(id); }

    private final DriverSimulationManager simulationManager = new DriverSimulationManager();

    public DriverSimulationManager getSimulationManager() {
        return simulationManager;
    }
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

    public void restoreRide(List<RoutePoint> points, boolean lockForm) {
        routePoints.setValue(points);
        if (lockForm) {
            rideLocked.setValue(true);
        } else {
            rideLocked.setValue(false);
        }
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
    public RideRequestDto buildRideRequest(
            List<RoutePoint> points,
            boolean pets,
            boolean baby
    ) {

        if (points == null || points.size() < 2) return null;
        if (selectedVehicleId.getValue() == null) return null;

        RideRequestDto payload = new RideRequestDto();

        payload.route = mapRoute(points);
        payload.schedule = mapSchedule();
        payload.vehicleTypeId = selectedVehicleId.getValue();
        payload.preferences = new RideRequestDto.Preferences();
        payload.preferences.pets = pets;
        payload.preferences.baby = baby;
        payload.linkedPassengers = passengerEmails.getValue();

        return payload;
    }

    private RideRequestDto.Route mapRoute(List<RoutePoint> points) {

        List<RoutePoint> sorted = new ArrayList<>(points);
        Collections.sort(sorted, Comparator.comparingInt(p -> p.orderIndex));

        RideRequestDto.Route route = new RideRequestDto.Route();
        route.points = new ArrayList<>();

        for (RoutePoint p : sorted) {
            RideRequestDto.Point dto = new RideRequestDto.Point();
            dto.lat = p.lat;
            dto.lng = p.lng;
            dto.orderIndex = p.orderIndex;
            dto.type = p.type.name();
            dto.address = p.address;
            route.points.add(dto);
        }

        return route;
    }

    private RideRequestDto.Schedule mapSchedule() {

        RideRequestDto.Schedule schedule = new RideRequestDto.Schedule();

        ScheduleType type = selectedScheduleType.getValue();
        schedule.type = type.name();

        if (type == ScheduleType.LATER) {
            schedule.startAt = buildScheduledIso();
        } else {
            schedule.startAt = java.time.Instant.now().toString();
        }

        return schedule;
    }

    private String buildScheduledIso() {

        String hhmm = scheduledTime.getValue();
        if (hhmm == null) return java.time.Instant.now().toString();

        String[] parts = hhmm.split(":");
        int h = Integer.parseInt(parts[0]);
        int m = Integer.parseInt(parts[1]);

        java.time.LocalDate today = java.time.LocalDate.now();

        java.time.LocalDateTime dateTime =
                java.time.LocalDateTime.of(
                                today.getYear(),
                                today.getMonth(),
                                today.getDayOfMonth(),
                                h,
                                m,
                                0
                        )
                        .plusHours(1); 

        java.time.ZonedDateTime zoned =
                dateTime.atZone(java.time.ZoneId.systemDefault());

        return zoned.toInstant().toString();
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

    public CreateFavoriteRouteDto buildFavoriteRoute(String name) {

        List<RoutePoint> points = routePoints.getValue();
        if (points == null || points.size() < 2) return null;

        CreateFavoriteRouteDto dto = new CreateFavoriteRouteDto();
        dto.name = name;
        dto.points = new ArrayList<>();

        for (RoutePoint p : points) {
            RoutePointDto point = new RoutePointDto();
            point.lat = p.lat;
            point.lng = p.lng;
            point.orderIndex = p.orderIndex;
            point.type = RoutePoint.toPointType(p.type);
            point.address = p.address;
            dto.points.add(point);
        }

        return dto;
    }


}
