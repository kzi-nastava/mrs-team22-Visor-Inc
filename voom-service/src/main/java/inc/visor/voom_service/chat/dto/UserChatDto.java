package inc.visor.voom_service.chat.dto;

import inc.visor.voom_service.auth.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserChatDto {
    private Long id;
    private String senderFirstName;
    private String senderLastName;
    private String email;
    private String profilePic;
    private List<ChatMessageDto> messages;

    public UserChatDto(UserDto user) {
        this.id = user.getId();
        this.senderFirstName = user.getFirstName();
        this.senderLastName = user.getLastName();
        this.email = user.getEmail();
        this.messages = List.of();
    }
}