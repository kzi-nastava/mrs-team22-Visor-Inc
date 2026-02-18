package inc.visor.voom_service.driver.dto;

import inc.visor.voom_service.driver.validation.PasswordConfirmable;
import inc.visor.voom_service.driver.validation.PasswordMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@PasswordMatch(message = "Password and confirm password do not match")
public class ActivateDriverRequestDto implements PasswordConfirmable {

    @NotBlank(message = "Activation token is required")
    private String token;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$",
            message = "Password must contain uppercase, lowercase letter and number"
    )
    private String password;

    @NotBlank(message = "Confirm password is required")

    private String confirmPassword;

    public ActivateDriverRequestDto() {
    }
}