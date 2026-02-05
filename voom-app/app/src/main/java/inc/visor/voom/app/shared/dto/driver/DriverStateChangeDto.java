package inc.visor.voom.app.shared.dto.driver;

import java.time.LocalDateTime;

public class DriverStateChangeDto {
    private long userId;
    private String currentState;
    private LocalDateTime performedAt;

    public DriverStateChangeDto() {
    }
}
