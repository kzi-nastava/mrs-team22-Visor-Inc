package inc.visor.voom_service.ride.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RideCancellationDto {
  private long userId;
  private String message;

  RideCancellationDto() {}


}
