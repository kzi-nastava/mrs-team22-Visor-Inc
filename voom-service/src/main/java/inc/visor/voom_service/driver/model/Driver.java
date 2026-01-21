package inc.visor.voom_service.driver.model;

import inc.visor.voom_service.auth.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

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

    @Override
    public String toString() {
        return "Driver{" +
                "id=" + id +
                ", user=" + user +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Driver driver = (Driver) o;
        return id == driver.id && Objects.equals(user, driver.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user);
    }
}
