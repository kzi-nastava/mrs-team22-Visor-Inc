package inc.visor.voom_service.auth.driver.dto;

import inc.visor.voom_service.domain.model.enums.DriverAccountStatus;

public class CreateDriverResponseDto {
    
    private Long id;
    private String email;
    private DriverAccountStatus status;

    public CreateDriverResponseDto() {};

    public CreateDriverResponseDto(Long id, String email, DriverAccountStatus status) {
        this.id = id;
        this.email = email;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public DriverAccountStatus getStatus() {
        return status;
    }

    public void setStatus(DriverAccountStatus status) {
        this.status = status;
    }
}