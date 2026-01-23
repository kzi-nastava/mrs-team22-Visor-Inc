package inc.visor.voom_service.driver.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AdminCreateDriverDto {
  private String firstName;
  private String lastName;
  private String phoneNumber;
  private String address;
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private LocalDateTime birthDate;
  private String email;
  private String password;
  private String userStatus;
  private int userRoleId;

  public AdminCreateDriverDto() {
  }
}
