package inc.visor.voom_service.shared;

import inc.visor.voom_service.osrm.dto.LatLng;
import inc.visor.voom_service.ride.model.enums.RoutePointType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoutePointDto {

    private Integer orderIndex;

    @NotNull(message="Latitude is required")
    private Double lat;

    @NotNull(message="Longitude is required")
    private Double lng;

    @NotBlank(message="Address is required")
    private String address;

    @NotNull(message="Type is required")
    private RoutePointType type;

    public RoutePointDto() {
    }

    public RoutePointDto(LatLng point, int i) {
        this.lat = point.lat();
        this.lng = point.lng();
        this.orderIndex = i;
    }
}
