package inc.visor.voom_service.ride.dto;

import jakarta.validation.constraints.Size;

public class RideReportRequestDto {

    @Size(min = 5, max = 500)
    String message;

    public String getMessage() {
        return message;
    }
}
