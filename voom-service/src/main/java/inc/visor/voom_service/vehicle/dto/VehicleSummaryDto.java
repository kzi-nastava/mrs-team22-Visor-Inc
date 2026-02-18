package inc.visor.voom_service.vehicle.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleSummaryDto {

    @NotBlank(message = "Vehicle type is required")
    private String vehicleType;

    @Min(value = 1900, message = "Year must be >= 1900")
    @Max(value = 2100, message = "Year must be valid")
    private int year;

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "License plate is required")
    @Size(max = 20, message = "License plate too long")
    private String licensePlate;

    private boolean babySeat;

    private boolean petFriendly;

    @Min(value = 1, message = "Vehicle must have at least 1 seat")
    @Max(value = 20, message = "Too many seats")
    private int numberOfSeats;

    @NotNull(message = "Driver id is required")
    private Long driverId;

    @PositiveOrZero(message = "Active hours must be >= 0")
    private double activeLast24Hours;

    public VehicleSummaryDto(String vehicleType, int year, String model, String licensePlate,
                             boolean babySeat, boolean petFriendly, int numberOfSeats,
                             Long driverId, double activeLast24Hours) {
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

    public VehicleSummaryDto(String type, int year, String model, String licensePlate,
                             boolean babySeat, boolean petFriendly, int numberOfSeats,
                             long id) {
        this.vehicleType = type;
        this.year = year;
        this.model = model;
        this.licensePlate = licensePlate;
        this.babySeat = babySeat;
        this.petFriendly = petFriendly;
        this.numberOfSeats = numberOfSeats;
        this.driverId = id;
    }

    public VehicleSummaryDto() {}
}
