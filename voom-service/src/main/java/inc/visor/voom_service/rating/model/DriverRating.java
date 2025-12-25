package inc.visor.voom_service.rating.model;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.ride.model.Ride;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "driver_rating")
public class DriverRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "driver_rating_id")
    private long driverRatingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id", referencedColumnName = "ride_id")
    private Ride ride;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", referencedColumnName = "driver_id")
    private Driver driver;

    @Column(name = "driver_rating")
    private int rating;

    @Column(name = "driver_rating_message")
    private String message;

    @Column(name = "driver_rating_date_time")
    private LocalDateTime dateTime;

}
