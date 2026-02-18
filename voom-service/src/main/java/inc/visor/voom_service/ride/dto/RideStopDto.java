package inc.visor.voom_service.ride.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import inc.visor.voom_service.osrm.dto.LatLng;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RideStopDto {

    @NotNull(message = "User id cannot be null")
    private Long userId;
    @NotNull(message = "Point cannot be null")
    private LatLng point;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp;

    public RideStopDto() {
    }
}
