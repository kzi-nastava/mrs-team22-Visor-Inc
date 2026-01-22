package inc.visor.voom_service.ride.dto;

import inc.visor.voom_service.person.model.Person;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RideCancelDto {
  private Long rideId;
  private Person person;
  private String message;

  RideCancelDto() {}


}
