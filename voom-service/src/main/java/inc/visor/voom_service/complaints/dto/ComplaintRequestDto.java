package inc.visor.voom_service.complaints.dto;

import jakarta.validation.constraints.Size;

public class ComplaintRequestDto {

    @Size(min = 5, max = 500)
    String message;

    public String getMessage() {
        return message;
    }
}
