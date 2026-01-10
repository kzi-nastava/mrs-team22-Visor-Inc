package inc.visor.voom_service.osrm.dto;

import java.time.LocalDateTime;
import java.util.List;

import inc.visor.voom_service.shared.RoutePointDto;

public class ScheduledRideDto {

    public Long rideId;
    public Long rideRequestId;

    public Long driverId;

    public LocalDateTime scheduledStartTime;

    public List<RoutePointDto> route;

    public String status;

    public ScheduledRideDto() {}

    public ScheduledRideDto(
            Long rideId,
            Long rideRequestId,
            Long driverId,
            LocalDateTime scheduledStartTime,
            List<RoutePointDto> route,
            String status
    ) {
        this.rideId = rideId;
        this.rideRequestId = rideRequestId;
        this.driverId = driverId;
        this.scheduledStartTime = scheduledStartTime;
        this.route = route;
        this.status = status;
    }


}
