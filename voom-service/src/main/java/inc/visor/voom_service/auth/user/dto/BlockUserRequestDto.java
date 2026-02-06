package inc.visor.voom_service.auth.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockUserRequestDto {

    @NotBlank(message = "Block reason is required")
    @Size(min = 5, max = 500, message = "Block reason must be between 5 and 500 characters")
    private String reason;

}
