package inc.visor.voom_service.shared;

import inc.visor.voom_service.ride.model.enums.RoutePointType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RoutePointDto {

    @NotNull(message="Order index is required")
    private Integer orderIndex;

    @NotNull(message="Latitude is required")
    private Double lat;

    @NotNull(message="Longitude is required")
    private Double lng;

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

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
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
