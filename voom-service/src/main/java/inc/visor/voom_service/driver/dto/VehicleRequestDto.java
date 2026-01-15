package inc.visor.voom_service.driver.dto;


import inc.visor.voom_service.vehicle.model.VehicleType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class VehicleRequestDto {

    @NotBlank(message="Model is required")
    private String model;

    @NotNull(message="Vehicle type is required")    
    private VehicleType type;

    @NotBlank(message="License plate is required")
    private String licensePlate;

    @Min(value=1, message="Number of seats must be at least 1")
    private Integer numberOfSeats;

    @NotNull(message="Baby transport information is required")
    private Boolean babyTransport;

    @NotNull(message="Pet transport information is required")
    private Boolean petTransport;

    public VehicleRequestDto() {};

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
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

    public Boolean getBabyTransport() {
        return babyTransport;
    }

    public void setBabyTransport(Boolean babyTransport) {
        this.babyTransport = babyTransport;
    }

    public Boolean getPetTransport() {
        return petTransport;
    }

    public void setPetTransport(Boolean petTransport) {
        this.petTransport = petTransport;
    }

    
}
