package inc.visor.voom_service.ride.dto;

import java.time.LocalDateTime;
import java.util.List;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.ride.model.Ride;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.RideRoute;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RideHistoryDto {
    private Long id;
    private RideStatus status;
    private RideRequest rideRequest;
    private RideRoute rideRoute;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private List<User> passengers;
    private User cancelledBy;

    public RideHistoryDto(Ride ride) {
        this.id = ride.getId();
        this.status = ride.getStatus();
        this.rideRequest = ride.getRideRequest();
        this.rideRoute = ride.getRideRequest().getRideRoute();
        this.startedAt = ride.getStartedAt();
        this.finishedAt = ride.getFinishedAt();
        this.passengers = ride.getPassengers();
        this.cancelledBy = ride.getRideRequest().getCancelledBy();
    }
}
