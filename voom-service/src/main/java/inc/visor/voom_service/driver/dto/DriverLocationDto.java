package inc.visor.voom_service.driver.dto;

import inc.visor.voom_service.driver.model.DriverStatus;
import lombok.Data;

@Data
public class DriverLocationDto {
    Long driverId;
    Double lat;
    Double lng;
    DriverStatus status;
    Integer etaSecondsUntilFinished;
}
