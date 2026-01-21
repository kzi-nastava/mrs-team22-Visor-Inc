package inc.visor.voom_service.auth.user.model;

import inc.visor.voom_service.driver.dto.DriverSummaryDto;
import inc.visor.voom_service.person.model.Person;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private long id;

    @OneToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "user_status", nullable = false)
    private UserStatus userStatus;

    @ManyToOne
    @JoinColumn(name = "user_role_id", nullable = false)
    private UserRole userRole;

    public User(String email, String password, UserStatus userStatus, UserRole userRole, Person person) {
        this.email = email;
        this.password = password;
        this.userStatus = userStatus;
        this.userRole = userRole;
        this.person = person;
    }

    public User(String email, String password, Person person, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.person = person;
        this.userRole = userRole;
        this.userStatus = UserStatus.PENDING;
    }

    public User() {}

    public User(long userId, DriverSummaryDto driverSummaryDto) {
        this.id = userId;
        this.email = driverSummaryDto.getEmail();
        this.userStatus = driverSummaryDto.getUserStatus();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", person=" + person +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", userStatus=" + userStatus +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(userStatus, user.userStatus) && Objects.equals(userRole, user.userRole) && Objects.equals(person, user.person);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, person, email, password, userStatus, userRole);
    }

}
