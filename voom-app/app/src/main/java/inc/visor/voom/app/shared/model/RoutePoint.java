package inc.visor.voom.app.shared.model;

import inc.visor.voom.app.shared.dto.RoutePointDto;
import inc.visor.voom.app.shared.model.enums.RoutePointType;

public class RoutePoint {
    public int orderIndex;
    public double lat;

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public RoutePointType getPointType() {
        return pointType;
    }

    public void setPointType(RoutePointType pointType) {
        this.pointType = pointType;
    }

    public double lng;
    public String address;
    public RoutePointType pointType;

    public RoutePoint(RoutePointDto dto) {
        this.lat = dto.lat;
        this.lng = dto.lng;
        this.address = dto.address;
        switch (dto.type) {
            case DROPOFF:
                this.pointType = RoutePointType.DROPOFF;
                break;
            case PICKUP:
                this.pointType = RoutePointType.PICKUP;
                break;
            case STOP:
                this.pointType = RoutePointType.STOP;
                break;
        };
        this.orderIndex = dto.orderIndex;

    }
}
