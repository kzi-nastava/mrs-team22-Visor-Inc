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
    private double activeLast24Hours;

    public VehicleSummaryDto(String vehicleType, int year, String model, String licensePlate, boolean babySeat, boolean petFriendly, int numberOfSeats, Long driverId, double activeLast24Hours) {
        this.vehicleType = vehicleType;
        this.year = year;
        this.model = model;
        this.licensePlate = licensePlate;
        this.babySeat = babySeat;
        this.petFriendly = petFriendly;
        this.numberOfSeats = numberOfSeats;
        this.driverId = driverId;
        this.activeLast24Hours = activeLast24Hours;
    }

    public VehicleSummaryDto(String type, int year, String model, String licensePlate, boolean babySeat, boolean petFriendly, int numberOfSeats, long id) {
        this.vehicleType = vehicleType;
        this.year = year;
        this.model = model;
        this.licensePlate = licensePlate;
        this.babySeat = babySeat;
        this.petFriendly = petFriendly;
        this.numberOfSeats = numberOfSeats;
        this.driverId = driverId;
    }

    public VehicleSummaryDto() {
    }

}
