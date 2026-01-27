package inc.visor.voom_service.auth.dto;

import inc.visor.voom_service.driver.validation.PasswordMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@PasswordMatch(message = "New password and confirm password do not match")
public class ResetPasswordDto {

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    private String password;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    private String confirmPassword;

    @NotBlank(message = "Token is required")
    private String token;
}
