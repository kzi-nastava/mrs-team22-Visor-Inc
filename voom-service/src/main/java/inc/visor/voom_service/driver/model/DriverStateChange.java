package inc.visor.voom_service.driver.model;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.driver.model.enums.DriverState;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "driver_state_changes")
public class DriverStateChange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "state_change_id", nullable = false)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User driver;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "current_state", nullable = false)
    private DriverState currentState;

    @Column(name = "performed_at", nullable = false)
    private LocalDateTime performedAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public DriverState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(DriverState currentState) {
        this.currentState = currentState;
    }

    public LocalDateTime getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = performedAt;
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
