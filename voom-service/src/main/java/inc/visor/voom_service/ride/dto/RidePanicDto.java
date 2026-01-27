package inc.visor.voom_service.ride.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RidePanicDto {
    @NotNull(message = "User id cannot be null")
    private long userId;

    public RidePanicDto() {
    }
}
