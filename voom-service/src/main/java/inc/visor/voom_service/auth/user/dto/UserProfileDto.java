package inc.visor.voom_service.auth.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import inc.visor.voom_service.auth.user.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserProfileDto {

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
    private String userStatus;

    @NotNull(message = "User role id is required")
    private int userRoleId;

    private String pfpUrl;

    private String status;

    public UserProfileDto() {
    }

    public UserProfileDto(User user) {
        this.id = user.getId();
        this.firstName = user.getPerson().getFirstName();
        this.lastName = user.getPerson().getLastName();
        this.pfpUrl = null;
        this.userStatus = user.getUserStatus().toString();
        this.email = user.getEmail();
        this.phoneNumber = user.getPerson().getPhoneNumber();
        this.birthDate = user.getPerson().getBirthDate();
        this.address = user.getPerson().getAddress();
        this.userRoleId = user.getUserRole().getId();
        this.status = user.getUserStatus().toString();
    }

}
