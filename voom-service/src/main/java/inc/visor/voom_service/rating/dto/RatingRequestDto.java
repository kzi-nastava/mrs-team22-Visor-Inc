package inc.visor.voom_service.rating.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RatingRequestDto {

    @Min(1)
    @Max(5)
    private Integer driverRating;
    @Min(1)
    @Max(5)
    private Integer vehicleRating;
    @Size(min = 0, max = 500)
    private String comment;
}
