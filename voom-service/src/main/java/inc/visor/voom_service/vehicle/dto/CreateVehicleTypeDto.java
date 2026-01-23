package inc.visor.voom_service.vehicle.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateVehicleTypeDto {
    private String type;
    private Double price;

    public CreateVehicleTypeDto() {
    }
}
