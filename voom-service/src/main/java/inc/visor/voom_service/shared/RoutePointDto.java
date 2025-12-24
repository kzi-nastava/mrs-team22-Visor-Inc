package inc.visor.voom_service.shared;

import inc.visor.voom_service.ride.model.enums.RoutePointType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RoutePointDto {

    @NotNull(message="Order index is required")
    private Integer orderIndex;

    @NotNull(message="Latitude is required")
    private Double latitude;

    @NotNull(message="Longitude is required")
    private Double longitude;

    @NotBlank(message="Address is required")
    private String address;

    @NotNull(message="Type is required")
    private RoutePointType type;

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public RoutePointType getType() {
        return type;
    }

    public void setType(RoutePointType type) {
        this.type = type;
    }

}
