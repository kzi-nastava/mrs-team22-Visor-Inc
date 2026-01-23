package inc.visor.voom_service.driver.dto;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.driver.model.DriverState;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChangeDriverStateRequest {
    private User driver;
    private DriverState currentState;
    private LocalDateTime performedAt;
}
