package inc.visor.voom_service.vehicle.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleSummaryDto {
    
    private String vehicleType;
    private int year;
    private String model;
    private String licensePlate;
    private boolean babySeat;
    private boolean petFriendly;
    private int numberOfSeats;
    private Long driverId;

    public VehicleSummaryDto(String vehicleType, int year, String model, String licensePlate, boolean babySeat, boolean petFriendly, int numberOfSeats, Long driverId) {
        this.vehicleType = vehicleType;
        this.year = year;
        this.model = model;
        this.licensePlate = licensePlate;
        this.babySeat = babySeat;
        this.petFriendly = petFriendly;
        this.numberOfSeats = numberOfSeats;
        this.driverId = driverId;
    }
}
