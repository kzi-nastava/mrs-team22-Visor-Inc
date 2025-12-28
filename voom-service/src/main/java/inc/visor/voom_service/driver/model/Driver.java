package inc.visor.voom_service.driver.model;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.person.model.Person;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "drivers")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "driver_id", nullable = false)
    private long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToOne
    @JoinColumn(name = "person_id", nullable = false, unique = true)
    private Person person;

    @Column(name = "driver_license", nullable = false, unique = true)
    private int driverLicense;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public int getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(int driverLicense) {
        this.driverLicense = driverLicense;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "id=" + id +
                ", user=" + user +
                ", person=" + person +
                ", driverLicense=" + driverLicense +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Driver driver = (Driver) o;
        return id == driver.id && driverLicense == driver.driverLicense && Objects.equals(user, driver.user) && Objects.equals(person, driver.person);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, person, driverLicense);
    }
}
