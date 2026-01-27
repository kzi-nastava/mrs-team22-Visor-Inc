package inc.visor.voom_service.vehicle.dto;

import inc.visor.voom_service.vehicle.model.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleTypeDto {

    @NotNull(message = "Vehicle type id is required")
    private long id;

    @NotBlank(message = "Vehicle type name cannot be blank ")
    private String type;

    @NotNull(message = "Vehicle type must have a price")
    private Double price;

    public VehicleTypeDto(VehicleType vehicleType) {
        this.id = vehicleType.getId();
        this.type = vehicleType.getType();
        this.price = vehicleType.getPrice();
    }

    public VehicleTypeDto() {
    }
}
