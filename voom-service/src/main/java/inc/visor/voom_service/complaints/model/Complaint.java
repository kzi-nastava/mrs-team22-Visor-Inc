package inc.visor.voom_service.complaints.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.ride.model.Ride;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"ride_id", "reporter_id"}),
        }
)
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id", nullable = false)
    @JsonBackReference
    private Ride ride;

    @ManyToOne()
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Column(length = 1000)
    private String message;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();
}

