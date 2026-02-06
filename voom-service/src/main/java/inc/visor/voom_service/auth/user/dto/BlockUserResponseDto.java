package inc.visor.voom_service.auth.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockUserResponseDto {

    private UserProfileDto user;
    private UserBlockNoteDto note;

    public BlockUserResponseDto(UserProfileDto user, UserBlockNoteDto note) {
        this.user = user;
        this.note = note;
    }
}
