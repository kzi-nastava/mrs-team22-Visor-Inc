package inc.visor.voom_service.auth.driver.dto;

import inc.visor.voom_service.auth.driver.validation.PasswordMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

 @PasswordMatch(message = "Password and confirm password do not match")
public class ActivateDriverRequestDto {

    @NotBlank(message="Activation token is required")
    private String token;

    @NotBlank(message="Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$",
        message = "Password must contain uppercase, lowercase letter and number"
    )
    private String password;

    @NotBlank(message="Confirm password is required")
   
    private String confirmPassword;

    public ActivateDriverRequestDto() {}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}