package inc.visor.voom_service.auth.user.model;

import java.util.Objects;

import inc.visor.voom_service.auth.user.dto.CreateUserDto;
import inc.visor.voom_service.auth.user.dto.UserProfileDto;
import inc.visor.voom_service.driver.dto.DriverSummaryDto;
import inc.visor.voom_service.person.model.Person;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "password")
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

    public User() {
    }

    public User(long userId, DriverSummaryDto dto) {
        this.id = userId;
        this.email = dto.getEmail();
        this.userStatus = dto.getUserStatus();
    }

    public User(CreateUserDto dto, Person person, UserRole userRole) {
        this.email = dto.getEmail();
        this.password = dto.getPassword();
        this.userStatus = UserStatus.valueOf(dto.getUserStatus());
        this.userRole = userRole;
        this.person = person;
    }

    public User(Person person, UserProfileDto dto, UserRole userRole) {
        this.person = person;
        this.id = dto.getId();
        this.email = dto.getEmail();
        this.userRole = userRole;
        this.userStatus = UserStatus.valueOf(dto.getUserStatus());
    }

    @Override
    public String toString() {
        return "User{"
                + "id=" + id
                + ", person=" + person
                + ", email='" + email + '\''
                + ", password='" + password + '\''
                + ", userStatus=" + userStatus
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return id == user.id && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(userStatus, user.userStatus) && Objects.equals(userRole, user.userRole) && Objects.equals(person, user.person);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, person, email, password, userStatus, userRole);
    }

}
