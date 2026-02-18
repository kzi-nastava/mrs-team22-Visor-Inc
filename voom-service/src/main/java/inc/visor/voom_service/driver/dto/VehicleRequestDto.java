package inc.visor.voom_service.driver.dto;


import inc.visor.voom_service.vehicle.model.VehicleType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleRequestDto {

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Vehicle type is required")
    private VehicleType type;

    @NotBlank(message = "License plate is required")
    private String licensePlate;

    @Min(value = 1, message = "Number of seats must be at least 1")
    private Integer numberOfSeats;

    @NotNull(message = "Baby transport information is required")
    private Boolean babyTransport;

    @NotNull(message = "Pet transport information is required")
    private Boolean petTransport;

    public VehicleRequestDto() {
    }

}
