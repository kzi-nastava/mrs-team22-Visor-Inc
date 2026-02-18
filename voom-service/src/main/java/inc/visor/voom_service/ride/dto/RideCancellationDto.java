package inc.visor.voom_service.ride.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RideCancellationDto {
    @NotNull(message = "User id cannot be null")
    private long userId;
    private String message;

    RideCancellationDto() {
    }


}
