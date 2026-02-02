package inc.visor.voom.app.user.home.model;

import com.google.gson.annotations.SerializedName;

public class RoutePoint {

    public enum PointType {
        PICKUP, STOP, DROPOFF
    }
    public double lat;
    public double lng;
    public String address;
    public int orderIndex;

    public PointType type;

    public RoutePoint(double lat, double lng, String address, int orderIndex, PointType type) {
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.orderIndex = orderIndex;
        this.type = type;
    }
}
