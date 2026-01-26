package inc.visor.voom_service.ride.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import inc.visor.voom_service.osrm.dto.LatLng;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class RideStopDto {

    private Long userId;
    private LatLng point;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp;

    public RideStopDto() {
    }
}
