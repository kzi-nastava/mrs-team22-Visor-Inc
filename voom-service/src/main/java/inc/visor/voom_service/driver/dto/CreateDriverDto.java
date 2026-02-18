package inc.visor.voom_service.driver.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDriverDto {

    private String email;

    private String firstName;

    private String lastName;

    private String birthDate;

    private String phoneNumber;

    private String address;

    private CreateVehicleDto vehicle;

    public CreateDriverDto() {
    }
}
