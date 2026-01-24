package inc.visor.voom_service.ride.dto;

import java.time.Instant;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RideRequestCreateDTO {

    @NotNull
    @Valid
    public RouteDTO route;

    @NotNull
    @Valid
    public ScheduleDTO schedule;

    @NotNull
    public Long vehicleTypeId;

    @NotNull
    @Valid
    public PreferencesDTO preferences;

    public List<String> linkedPassengers;

    public List<DriverLocationDTO> freeDriversSnapshot;

    public static class RouteDTO {
        @NotNull
        @Size(min = 2, message = "At least two route points are required")
        @Valid
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
        @NotBlank
        public String type; 
        public Instant startAt;
    }

    public static class PreferencesDTO {
        public boolean baby;
        public boolean pets;
    }

    public static class DriverLocationDTO {
        public Long driverId;
        public double lat;
        public double lng;
        
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

    public List<DriverLocationDTO> getFreeDriversSnapshot() {
        return freeDriversSnapshot;
    }
}