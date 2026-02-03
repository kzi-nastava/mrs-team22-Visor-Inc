package inc.visor.voom.app.shared.dto;


import java.util.List;

public class ScheduledRideDto {

    public Long rideId;

    public Long rideRequestId;

    public Long driverId;

    public String scheduledStartTime;

    public List<RoutePointDto> route;

    public String status;

    public long creatorId;

    public ScheduledRideDto() {}

    public ScheduledRideDto(
            Long rideId,
            Long rideRequestId,
            Long driverId,
            String scheduledStartTime,
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
