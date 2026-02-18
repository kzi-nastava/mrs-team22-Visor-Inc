package inc.visor.voom_service.ride.dto;

import inc.visor.voom_service.ride.model.RoutePoint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

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
        public Integer orderIndex;
        public String type;
        public String address;

        public RoutePointDto(RoutePoint point) {
            this.lat = point.getLatitude();
            this.lng = point.getLongitude();
            this.address = point.getAddress();
            this.orderIndex = point.getOrderIndex();
            this.type = point.getPointType().toString();
        }

        public RoutePointDto() {
        }
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