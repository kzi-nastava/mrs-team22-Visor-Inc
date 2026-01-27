package inc.visor.voom_service.driver.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminCreateDriverDto {
  @NotNull(message = "User id cannot be null")
  private long userId;

  public AdminCreateDriverDto() {
  }
}
