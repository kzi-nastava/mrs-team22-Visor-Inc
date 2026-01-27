package inc.visor.voom_service.vehicle.dto;

import inc.visor.voom_service.vehicle.model.Vehicle;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleDto {
    @NotNull(message = "Vehicle type id is required")
    private long id;

    @NotNull(message = "Vehicle type is required")
    private Long vehicleTypeId;

    @NotNull(message = "Driver is required")
    private Long driverId;

    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year is not valid")
    private Integer year;

    @NotBlank(message = "Model is required")
    @Size(min = 2, max = 255, message = "Model must be at least 2 characters")
    private String model;

    @NotBlank(message = "License plate is required")
    @Size(max = 20, message = "License plate is too long")
    private String licensePlate;

    private boolean babySeat;
    private boolean petFriendly;

    @NotNull(message = "Number of seats is required")
    @Min(value = 1, message = "Vehicle must have at least 1 seat")
    @Max(value = 20, message = "Vehicle cannot have more than 20 seats")
    private Integer numberOfSeats;

    public VehicleDto(Vehicle vehicle) {
        id = vehicle.getId();
        driverId = vehicle.getDriver().getId();
        vehicleTypeId = vehicle.getVehicleType().getId();
        year = vehicle.getYear();
        model = vehicle.getModel();
        licensePlate = vehicle.getLicensePlate();
        babySeat = vehicle.isBabySeat();
        petFriendly = vehicle.isPetFriendly();
        numberOfSeats = vehicle.getNumberOfSeats();
    }

    public VehicleDto() {
    }
}
