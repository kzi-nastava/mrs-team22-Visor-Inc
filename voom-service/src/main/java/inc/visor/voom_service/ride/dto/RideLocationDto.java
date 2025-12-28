package inc.visor.voom_service.ride.dto;

import lombok.Data;

@Data
public class RideLocationDto {
    private Long rideId;
    private Long driverId;
    private Double lat;
    private Double lng;
}
