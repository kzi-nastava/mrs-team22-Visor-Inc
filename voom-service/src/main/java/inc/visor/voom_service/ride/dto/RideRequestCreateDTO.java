package inc.visor.voom_service.ride.dto;

import java.time.Instant;
import java.util.List;

public class RideRequestCreateDTO {

    public RouteDTO route;
    public ScheduleDTO schedule;
    public Long vehicleTypeId;
    public PreferencesDTO preferences;
    public List<String> linkedPassengers;

    public static class RouteDTO {
        public List<RoutePointDTO> points;
    }

    public static class RoutePointDTO {
        public double lat;
        public double lng;
        public int order;
        public String type; 
        public String address;
    }

    public static class ScheduleDTO {
        public String type; 
        public Instant startAt;
    }

    public static class PreferencesDTO {
        public boolean baby;
        public boolean pets;
    }

    public RouteDTO getRoute() {
        return route;
    }

    public ScheduleDTO getSchedule() {
        return schedule;
    }

    public Long getVehicleTypeId() {
        return vehicleTypeId;
    }

    public PreferencesDTO getPreferences() {
        return preferences;
    }

    public List<String> getLinkedPassengers() {
        return linkedPassengers;
    }

}