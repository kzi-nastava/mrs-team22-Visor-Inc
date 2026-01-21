package inc.visor.voom_service.person.model;

import java.time.LocalDateTime;
import java.util.Objects;

import inc.visor.voom_service.auth.dto.RegistrationDto;
import inc.visor.voom_service.auth.user.dto.UserProfileDto;
import inc.visor.voom_service.driver.dto.DriverSummaryDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "person")
@Getter
@Setter
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id", nullable = false)
    private long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "birth_date", nullable = false)
    private LocalDateTime birthDate;

    public Person() {}

    public Person(RegistrationDto dto) {
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.phoneNumber = dto.getPhoneNumber();
        this.address = dto.getAddress();
        this.birthDate = dto.getBirthDate();
    }

    public Person(long personId, DriverSummaryDto dto) {
        this.id = personId;
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.phoneNumber = dto.getPhoneNumber();
        this.address = dto.getAddress();
        this.birthDate = dto.getBirthDate();
    }

    public Person(long personId, UserProfileDto dto) {
        this.id = personId;
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.phoneNumber = dto.getPhoneNumber();
        this.address = dto.getAddress();
        this.birthDate = dto.getBirthDate();
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", birthDate=" + birthDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id && Objects.equals(firstName, person.firstName) && Objects.equals(lastName, person.lastName) && Objects.equals(phoneNumber, person.phoneNumber) && Objects.equals(address, person.address) && Objects.equals(birthDate, person.birthDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, phoneNumber, address, birthDate);
    }
}
