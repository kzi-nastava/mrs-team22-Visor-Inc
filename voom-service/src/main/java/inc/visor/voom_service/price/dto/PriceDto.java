package inc.visor.voom_service.price.dto;

import inc.visor.voom_service.price.model.Price;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PriceDto {
    private long priceId;
    private long vehicleTypeId;
    private Double pricePerKm;

    public PriceDto(Price price) {
        this.priceId = price.getPriceId();
        this.vehicleTypeId = price.getVehicleType().getId();
        this.pricePerKm = price.getPricePerKm();
    }
}
