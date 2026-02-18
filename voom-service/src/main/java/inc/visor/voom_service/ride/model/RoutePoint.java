package inc.visor.voom_service.ride.model;

import inc.visor.voom_service.osrm.dto.LatLng;
import inc.visor.voom_service.ride.model.enums.RoutePointType;
import inc.visor.voom_service.shared.RoutePointDto;
import jakarta.persistence.*;

@Entity
@Table(name = "route_point")
public class RoutePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_point_id", nullable = false)
    private long id;

    @Column(name = "order_index", nullable = true)
    private int orderIndex;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Column(name = "address", nullable = true)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "point_type", nullable = true)
    private RoutePointType pointType;

    public RoutePoint() {
    }

    public RoutePoint(LatLng point) {
        this.latitude = point.lat();
        this.longitude = point.lng();
        this.pointType = RoutePointType.STOPPED;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

    public RoutePointDto toDto() {
        RoutePointDto dto = new RoutePointDto();
        dto.setLat(this.latitude);
        dto.setLng(this.longitude);
        dto.setAddress(this.address);
        dto.setType(this.pointType);
        return dto;
    }

}
