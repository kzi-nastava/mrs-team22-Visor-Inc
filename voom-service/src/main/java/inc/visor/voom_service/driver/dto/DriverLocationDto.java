package inc.visor.voom_service.driver.dto;

import inc.visor.voom_service.driver.model.enums.DriverActivityStatus;
import lombok.Data;

@Data
public class DriverLocationDto {
    Long driverId;
    Double lat;
    Double lng;
    DriverActivityStatus status;
    Integer etaSecondsUntilFinished;
}
