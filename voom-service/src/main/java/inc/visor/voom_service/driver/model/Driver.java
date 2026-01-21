package inc.visor.voom_service.driver.model;

import java.util.Objects;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.driver.model.enums.DriverStatus;
import inc.visor.voom_service.person.model.Person;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "drivers")
@Getter
@Setter
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "driver_id", nullable = false)
    private long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "driver_status", nullable = false)
    private DriverStatus status;

    @Override
    public String toString() {
        return "Driver{" +
                "id=" + id +
                ", user=" + user +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Driver driver = (Driver) o;
        return id == driver.id && Objects.equals(user, driver.user) && status == driver.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, status);
    }
}
