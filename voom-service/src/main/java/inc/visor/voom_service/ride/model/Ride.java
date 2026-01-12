package inc.visor.voom_service.ride.model;

import java.time.LocalDateTime;
import java.util.List;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ride")
public class Ride {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ride_id", nullable = false)
    private long id;

    @OneToOne
    @JoinColumn(name = "ride_request_id", nullable = false)
    private RideRequest rideRequest;

    @OneToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RideStatus status;

    @Column(name = "started_at", nullable = true)
    private LocalDateTime startedAt;

    @Column(name = "finished_at", nullable = true)
    private LocalDateTime finishedAt;

    @OneToMany(fetch = FetchType.LAZY)
    @Column(name = "passengers", nullable = false)
    private List<User> passengers;

    public Ride() {}

}
