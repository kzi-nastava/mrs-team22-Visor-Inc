package inc.visor.voom_service.rating.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.rating.validations.ValidRatingTime;
import inc.visor.voom_service.ride.model.Ride;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"ride_id", "rater_id"}),
        }
)
@Data
@ValidRatingTime
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(1)
    @Max(5)
    private Integer driverRating;

    @Min(1)
    @Max(5)
    private Integer vehicleRating;

    @Size(max = 500)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ride_id", nullable = false)
    @JsonBackReference
    private Ride ride;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rater_id", nullable = false)
    private User rater;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();
}

