package inc.visor.voom.app.user.home.model;

import com.google.gson.annotations.SerializedName;

import inc.visor.voom.app.shared.dto.RoutePointDto;
import inc.visor.voom.app.shared.dto.RoutePointType;

public class RoutePoint {

    public enum PointType {
        PICKUP, STOP, DROPOFF
    }
    public double lat;
    public double lng;
    public String address;
    public int orderIndex;

    public PointType type;

    public RoutePoint(double lat, double lng, String address,
                      Integer orderIndex, PointType type) {
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.orderIndex = orderIndex != null ? orderIndex : 0;
        this.type = type;
    }

    public RoutePoint(RoutePointDto dto) {
        this.lat = dto.lat;
        this.lng = dto.lng;
        this.address = dto.address;
        this.orderIndex = dto.orderIndex != null ? dto.orderIndex : 0;
        this.type = toPointType(dto.type);
    }

    public static RoutePointType toPointType(PointType type) {
        if (type == PointType.STOP) {
            return RoutePointType.STOP;
        } else if (type == PointType.PICKUP) {
            return RoutePointType.PICKUP;
        } else {
            return RoutePointType.DROPOFF;
        }
    }

    public static PointType toPointType(RoutePointType type) {
        if (type == RoutePointType.STOP) {
            return PointType.STOP;
        } else if (type == RoutePointType.PICKUP) {
            return PointType.PICKUP;
        } else {
            return PointType.DROPOFF;
        }
    }
}
