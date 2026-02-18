package inc.visor.voom_service.route.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteEstimateResponseDto {

    private int duration;
    private double distance;

    public RouteEstimateResponseDto(int duration, double distance) {
        this.duration = duration;
        this.distance = distance;
    }

    public RouteEstimateResponseDto() {
    }
}
