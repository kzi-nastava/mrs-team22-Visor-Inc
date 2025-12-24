package inc.visor.voom_service.ride.dto;

import inc.visor.voom_service.person.model.Person;

public class RideCancelDto {
  private Long rideId;
  private Person person;
  private String message;

  public Long getRideId() {
    return rideId;
  }

  public void setRideId(Long rideId) {
    this.rideId = rideId;
  }

  public Person getPerson() {
    return person;
  }

  public void setPerson(Person person) {
    this.person = person;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  RideCancelDto() {}


}
