package inc.visor.voom_service.ride.model;

import java.time.LocalDateTime;
import java.util.List;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.ride.model.enums.RideRequestStatus;
import inc.visor.voom_service.ride.model.enums.ScheduleType;
import inc.visor.voom_service.vehicle.model.VehicleType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "ride_request")
@Data
public class RideRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ride_request_id", nullable = false)
    private long id;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ride_route_id", nullable = false)
    private RideRoute rideRoute;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RideRequestStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", nullable = false)
    private ScheduleType scheduleType;

    @Column(name = "scheduled_time", nullable = true)
    private LocalDateTime scheduledTime;

    @ManyToOne
    @JoinColumn(name = "vehicle_type_id", nullable = false)
    private VehicleType vehicleType;

    @Column(name = "baby_transport", nullable = false)
    private boolean babyTransport;

    @Column(name = "pet_transport", nullable = false)
    private boolean petTransport;

    @Column(name = "calculated_price", nullable = false)
    private double calculatedPrice;

    @ElementCollection
    @CollectionTable(
        name = "ride_request_linked_passengers",
        joinColumns = @JoinColumn(name = "ride_request_id")
    )
    @Column(name = "email", nullable = false)
    private List<String> linkedPassengerEmails;

    @ManyToOne
    @JoinColumn(name = "cancelled_by_user_id", nullable = true)
    private User cancelledBy;

    @Column(name = "cancellation_reason", nullable = true)
    private String reason;

    public RideRequest() {}

}
