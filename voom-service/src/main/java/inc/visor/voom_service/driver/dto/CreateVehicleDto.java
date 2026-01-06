package inc.visor.voom_service.driver.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateVehicleDto {

    @NotBlank(message = "Vehicle model is required")
    private String model;

    @NotBlank(message = "Vehicle type is required")
    private String vehicleType;

    @NotBlank(message = "License plate is required")
    private String licensePlate;

    @NotNull(message = "Number of seats is required")
    @Min(value = 1, message = "Vehicle must have at least 1 seat")
    @Max(value = 8, message = "Vehicle cannot have more than 8 seats")
    private Integer numberOfSeats;

    @NotNull
    private Boolean babySeat;

    @NotNull
    private Boolean petFriendly;

    public CreateVehicleDto() {}

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public Boolean getBabySeat() {
        return babySeat;
    }

    public void setBabySeat(Boolean babySeat) {
        this.babySeat = babySeat;
    }

    public Boolean getPetFriendly() {
        return petFriendly;
    }

    public void setPetFriendly(Boolean petFriendly) {
        this.petFriendly = petFriendly;
    }
}
