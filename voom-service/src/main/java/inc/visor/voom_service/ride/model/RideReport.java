package inc.visor.voom_service.ride.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class RideReport {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private Long rideId;

    @Column(length = 1000)
    private String message;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();
}

