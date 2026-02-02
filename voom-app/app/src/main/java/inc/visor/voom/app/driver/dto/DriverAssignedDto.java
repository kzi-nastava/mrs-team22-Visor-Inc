package inc.visor.voom.app.driver.dto;

import java.util.List;

import inc.visor.voom.app.shared.dto.RoutePointDto;

public class DriverAssignedDto {
    public Long rideId;
    public Long driverId;
    public List<RoutePointDto> route;

    public DriverAssignedDto() {}

}
