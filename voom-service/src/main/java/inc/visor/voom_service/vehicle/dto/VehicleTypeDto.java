package inc.visor.voom_service.vehicle.dto;

import inc.visor.voom_service.vehicle.model.VehicleType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleTypeDto {
    private long id;
    private String type;
    private Double price;

    public VehicleTypeDto(VehicleType vehicleType) {
        this.id = vehicleType.getId();
        this.type = vehicleType.getType();
        this.price = vehicleType.getPrice();
    }

    public VehicleTypeDto() {
    }
}
