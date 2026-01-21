package inc.visor.voom_service.driver.dto;

import inc.visor.voom_service.auth.user.model.UserStatus;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.enums.DriverActivityStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DriverSummaryDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String pfpUrl;
    private DriverActivityStatus status;
    private UserStatus userStatus;
    private String email;
    private String phoneNumber;
    private LocalDateTime birthDate;
    private String address;

    public DriverSummaryDto() {
    }

    public DriverSummaryDto(Driver driver) {
        this.id = driver.getId();
        this.firstName = driver.getUser().getPerson().getFirstName();
        this.lastName = driver.getUser().getPerson().getLastName();
        this.email = driver.getUser().getEmail();
        this.phoneNumber = driver.getUser().getPerson().getPhoneNumber();
        this.birthDate = driver.getUser().getPerson().getBirthDate();
        this.userStatus = driver.getUser().getUserStatus();
        this.address = driver.getUser().getPerson().getAddress();
    }

    @Override
    public String toString() {
        return "DriverSummaryDto{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", pfpUrl='" + pfpUrl + '\'' +
                ", status=" + status +
                ", userStatus=" + userStatus +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", birthDate=" + birthDate +
                ", address='" + address + '\'' +
                '}';
    }
}
