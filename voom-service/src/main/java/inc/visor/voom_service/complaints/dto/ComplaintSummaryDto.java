package inc.visor.voom_service.complaints.dto;

import inc.visor.voom_service.auth.user.dto.UserProfileDto;
import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.complaints.model.Complaint;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ComplaintSummaryDto {
    String message;
    LocalDateTime time;
    UserProfileDto reporter;

    public ComplaintSummaryDto(Complaint complaint) {
        this.message = complaint.getMessage();
        this.time = complaint.getCreatedAt();
        this.reporter = new UserProfileDto(complaint.getReporter());
    }
}
