package inc.visor.voom_service.driver.dto;

import inc.visor.voom_service.ride.model.enums.DriverAccountStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDriverResponseDto {

    private Long id;
    private String email;
    private DriverAccountStatus status;

    public CreateDriverResponseDto() {
    }

    public CreateDriverResponseDto(Long id, String email, DriverAccountStatus status) {
        this.id = id;
        this.email = email;
        this.status = status;
    }
}