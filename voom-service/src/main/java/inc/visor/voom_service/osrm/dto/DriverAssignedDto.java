package inc.visor.voom_service.osrm.dto;

import java.util.List;

import inc.visor.voom_service.ride.model.RoutePoint;
import inc.visor.voom_service.shared.RoutePointDto;

public class DriverAssignedDto {

    public Long rideId;
    public Long driverId;
    public List<RoutePointDto> route;

    public DriverAssignedDto() {
    }

    public DriverAssignedDto(Long rideId, Long driverId, List<RoutePoint> routePoints) {
        this.rideId = rideId;
        this.driverId = driverId;
        this.route = routePoints.stream().map(p -> {
            RoutePointDto rp = new RoutePointDto();
            rp.setLat(p.getLatitude());
            rp.setLng(p.getLongitude());
            rp.setOrderIndex(p.getOrderIndex());
            rp.setType(p.getPointType());
            rp.setAddress(p.getAddress());
            return rp;
        }).toList();
    }

    @Override
    public String toString() {
        return "DriverAssignedDto{"
                + "rideId=" + rideId
                + ", driverId=" + driverId
                + '}';
    }

}
