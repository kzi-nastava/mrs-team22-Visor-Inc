package inc.visor.voom_service.auth.user.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import inc.visor.voom_service.auth.user.model.UserBlockNote;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBlockNoteDto {

    private long id;
    private long userId;
    private long adminId;

    private String reason;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;

    private boolean active;

    public UserBlockNoteDto() {}

    public UserBlockNoteDto(UserBlockNote note) {
        this.id = note.getId();
        this.userId = note.getUser().getId();
        this.adminId = note.getAdmin().getId();
        this.reason = note.getReason();
        this.createdAt = note.getCreatedAt();
        this.active = note.isActive();
    }
}
