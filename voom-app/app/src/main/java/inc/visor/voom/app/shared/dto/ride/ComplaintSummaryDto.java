package inc.visor.voom.app.shared.dto.ride;

import java.time.LocalDateTime;

import inc.visor.voom.app.admin.users.dto.UserProfileDto;

public class ComplaintSummaryDto {
    String message;
    LocalDateTime time;
    UserProfileDto reporter;

    public ComplaintSummaryDto() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public UserProfileDto getReporter() {
        return reporter;
    }

    public void setReporter(UserProfileDto reporter) {
        this.reporter = reporter;
    }
}
