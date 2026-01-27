package inc.visor.voom_service.driver.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import inc.visor.voom_service.auth.user.model.UserStatus;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.model.DriverStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DriverSummaryDto {

    @NotNull(message = "User id must not be null")
    private long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 255, message = "First name must be between 2 and 255 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 255, message = "Last name must be between 2 and 255 characters")
    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Size(min = 2, max = 55, message = "Phone number must be between 2 and 55 characters")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    @Size(min = 2, max = 255, message = "Address must be between 2 and 255 characters")
    private String address;

    @NotNull(message = "Birth date is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime birthDate;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must be at most 255 characters")
    private String email;

    @NotBlank(message = "User status is required")
    private UserStatus userStatus;

    @NotNull(message = "User role id is required")
    private int userRoleId;

    private String pfpUrl;

    @NotNull(message = "Driver status is required")
    private DriverStatus status;

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
        this.status = driver.getStatus();
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
