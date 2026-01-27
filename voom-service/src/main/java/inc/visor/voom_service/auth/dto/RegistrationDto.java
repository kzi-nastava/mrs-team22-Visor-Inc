package inc.visor.voom_service.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RegistrationDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must be at most 255 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    private String password;
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 255, message = "First name must be between 2 and 255 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 255, message = "Last name must be between 2 and 255 characters")
    private String lastName;

    @NotNull(message = "Birth date is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime birthDate;

    @NotBlank(message = "Address is required")
    @Size(min = 2, max = 255, message = "Address must be between 2 and 255 characters")
    private String address;

    @NotBlank(message = "Phone number is required")
    @Size(min = 2, max = 55, message = "Phone number must be between 2 and 55 characters")
    private String phoneNumber;

    @NotBlank(message = "User type is required")
    private String userType;

    public RegistrationDto() {
    }
}
