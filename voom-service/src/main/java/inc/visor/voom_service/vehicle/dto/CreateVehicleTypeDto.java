package inc.visor.voom_service.vehicle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateVehicleTypeDto {
    @NotBlank(message = "Vehicle type name cannot be blank ")
    private String type;

    @NotNull(message = "Vehicle type must have a price")
    private Double price;

    public CreateVehicleTypeDto() {
    }
}
