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
}
