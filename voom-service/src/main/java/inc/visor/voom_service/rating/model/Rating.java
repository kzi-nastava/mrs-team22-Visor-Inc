package inc.visor.voom_service.rating.model;

import inc.visor.voom_service.ride.model.Ride;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
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
    private Ride ride;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();
}

