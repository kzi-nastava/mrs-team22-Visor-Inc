package inc.visor.voom_service.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyTokenDto {
    @NotBlank(message = "Token is required")
    private String token;
}