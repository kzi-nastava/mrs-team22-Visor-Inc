package inc.visor.voom_service.driver.model;

import inc.visor.voom_service.driver.dto.DriverStateChangeDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "driver_state_changes")
@Getter
@Setter
public class DriverStateChange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "state_change_id", nullable = false)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "state", nullable = false)
    private DriverState currentState;

    @Column(name = "performed_at", nullable = false)
    private LocalDateTime performedAt;

    public DriverStateChange(Driver driver, DriverStateChangeDto dto) {
        this.driver = driver;
        this.currentState = DriverState.valueOf(dto.getCurrentState());
        this.performedAt = dto.getPerformedAt();
    }

    public DriverStateChange() {
    }

    @Override
    public String toString() {
        return "DriverStateChange{" +
                "id=" + id +
                ", driver=" + driver +
                ", currentState=" + currentState +
                ", performedAt=" + performedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DriverStateChange that = (DriverStateChange) o;
        return id == that.id && Objects.equals(driver, that.driver) && currentState == that.currentState && Objects.equals(performedAt, that.performedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, driver, currentState, performedAt);
    }
}
