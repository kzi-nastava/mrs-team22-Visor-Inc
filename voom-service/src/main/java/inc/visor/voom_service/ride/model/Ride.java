package inc.visor.voom_service.ride.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.complaints.model.Complaint;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.rating.model.Rating;
import inc.visor.voom_service.ride.model.enums.RideStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ride")
@Data
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ride_id", nullable = false)
    private long id;

    @OneToOne
    @JoinColumn(name = "ride_request_id", nullable = false)
    private RideRequest rideRequest;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = true)
    private Driver driver;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    private RideStatus status;

    @Column(name = "started_at", nullable = true)
    private LocalDateTime startedAt;

    @Column(name = "finished_at", nullable = true)
    private LocalDateTime finishedAt;

    @Column(name = "reminder_sent", nullable = true)
    private boolean reminderSent = false;

    @ManyToMany
    @JoinTable(
            name = "ride_passenger",
            joinColumns = @JoinColumn(name = "ride_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> passengers;

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Complaint> complaints;

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Rating> ratings;

    public Ride() {
    }

    public String getPickupAddress() {
        return rideRequest.getRideRoute().getPickupPoint().getAddress();
    }

    public String getDropoffAddress() {
        return rideRequest.getRideRoute().getDropoffPoint().getAddress();
    }
}
