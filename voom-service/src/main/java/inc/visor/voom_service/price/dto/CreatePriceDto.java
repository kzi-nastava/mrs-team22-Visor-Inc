package inc.visor.voom_service.price.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePriceDto {

    private long vehicleTypeId;
    private Double pricePerKm;

}
