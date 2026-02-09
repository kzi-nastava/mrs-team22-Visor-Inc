package inc.visor.voom.app.shared.dto;

import java.util.List;

import inc.visor.voom.app.shared.model.RideRequest;
import inc.visor.voom.app.shared.model.RideRoute;
import inc.visor.voom.app.shared.model.User;
import inc.visor.voom.app.shared.model.enums.RideStatus;

public class RideHistoryDto {
    public Long id;
    public RideStatus status;
    public RideRequest rideRequest;
    public RideRoute rideRoute;
    public String startedAt;
    public String finishedAt;
    public List<User> passengers;
    public User cancelledBy;
    public boolean isExpanded;

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

    public String getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    public String getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(String finishedAt) {
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

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean b) {
        this.isExpanded = b;
    }
}
