package inc.visor.voom_service.ride.dto;

import java.time.Instant;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RideRequestCreateDto {

    @NotNull
    @Valid
    public RideRequestCreateDto.RouteDto route;

    @NotNull
    @Valid
    public RideRequestCreateDto.ScheduleDto schedule;

    @NotNull
    public Long vehicleTypeId;

    @NotNull
    @Valid
    public RideRequestCreateDto.PreferencesDto preferences;

    public List<String> linkedPassengers;

    public List<DriverLocationDto> freeDriversSnapshot;

    public static class RouteDto {
        @NotNull
        @Size(min = 2, message = "At least two route points are required")
        @Valid
        public List<RoutePointDto> points;
    }

    public static class RoutePointDto {
        public double lat;
        public double lng;
        public int order;
        public String type;
        public String address;
    }

    public static class ScheduleDto {
        @NotBlank
        public String type; 
        public Instant startAt;
    }

    public static class PreferencesDto {
        public boolean baby;
        public boolean pets;
    }

    public static class DriverLocationDto {
        public Long driverId;
        public double lat;
        public double lng;
        
    }

}