package inc.visor.voom_service.domain.ride;

import java.time.LocalDateTime;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.domain.ride.enums.RideStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ride")
public class Ride {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ride_id", nullable = false)
    private long id;

    @OneToOne
    @JoinColumn(name = "ride_request_id", nullable = false)
    private RideRequest rideRequest;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User driver;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RideStatus status;

    @Column(name = "started_at", nullable = true)
    private LocalDateTime startedAt;

    @Column(name = "finished_at", nullable = true)
    private LocalDateTime finishedAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RideRequest getRideRequest() {
        return rideRequest;
    }

    public void setRideRequest(RideRequest rideRequest) {
        this.rideRequest = rideRequest;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
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



}
