package inc.visor.voom_service.driver.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.driver.model.DriverState;
import inc.visor.voom_service.driver.model.DriverStateChange;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DriverStateChangeDto {
    @NotNull(message = "User id must not be null")
    private long userId;

    @NotBlank(message = "Current state must not be blank")
    private String currentState;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime performedAt;

    public DriverStateChangeDto(DriverStateChange driverState) {
        this.userId = driverState.getId();
        this.currentState = driverState.getCurrentState().toString();
        this.performedAt = driverState.getPerformedAt();
    }

    public DriverStateChangeDto() {
    }
}
