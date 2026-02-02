package inc.visor.voom.app.driver.dto;


import java.util.List;

import inc.visor.voom.app.shared.dto.RoutePointDto;

public class ActiveRideDto {
    public Long rideId;
    public String status;
    public List<RoutePointDto> routePoints;
    public Long driverId;

    public String toString() {
        return this.routePoints.toString();
    }
}
