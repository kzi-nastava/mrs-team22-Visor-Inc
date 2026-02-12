package inc.visor.voom.app.shared.dto;

import com.google.gson.annotations.SerializedName;

import inc.visor.voom.app.user.home.model.RoutePoint;

public class RoutePointDto {
    @SerializedName("order")
    public Integer orderIndex;

    public Double lat;

    public Double lng;

    public String address;

    public RoutePointType type;

    public RoutePointDto() {}

    public String toString() {
        return String.valueOf(this.lat) + " " + String.valueOf(this.lng) + String.valueOf(this.orderIndex);

    }

    public static RoutePoint.PointType toPointType(RoutePointType type) {
        if (type == RoutePointType.STOP) {
            return RoutePoint.PointType.STOP;
        } else if (type == RoutePointType.PICKUP) {
            return RoutePoint.PointType.PICKUP;
        } else {
            return RoutePoint.PointType.DROPOFF;
        }
    }

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
