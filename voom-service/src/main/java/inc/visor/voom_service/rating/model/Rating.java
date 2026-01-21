package inc.visor.voom_service.rating.model;

import inc.visor.voom_service.ride.model.Ride;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
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

    private LocalDateTime createdAt = LocalDateTime.now();
}

