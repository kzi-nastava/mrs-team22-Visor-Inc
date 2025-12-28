package inc.visor.voom_service.ride.dto;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.ride.model.RideRequest;
import inc.visor.voom_service.ride.model.RideRoute;
import inc.visor.voom_service.ride.model.enums.RideStatus;

import java.time.LocalDateTime;
import java.util.List;

public class RideHistoryDto {
    private Long id;
    private RideStatus status;
    private RideRequest rideRequest;
    private RideRoute rideRoute;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private List<User> passengers;
    private User cancelledBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public RideRequest getRideRequest() {
        return rideRequest;
    }

    public void setRideRequest(RideRequest rideRequest) {
        this.rideRequest = rideRequest;
    }

    public RideRoute getRideRoute() {
        return rideRoute;
    }

    public void setRideRoute(RideRoute rideRoute) {
        this.rideRoute = rideRoute;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public List<User> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<User> passengers) {
        this.passengers = passengers;
    }

    public User getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(User cancelledBy) {
        this.cancelledBy = cancelledBy;
    }
}
