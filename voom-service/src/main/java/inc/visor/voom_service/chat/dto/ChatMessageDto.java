package inc.visor.voom_service.chat.dto;

import inc.visor.voom_service.chat.model.ChatMessage;
import inc.visor.voom_service.chat.validations.ValidEmailOrAdmin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageDto {
    @NotBlank
    @Size(min = 1)
    private String content;

    @ValidEmailOrAdmin
    private String senderEmail;

    @NotNull
    @NotBlank
    private String senderFirstName;

    @NotNull
    @NotBlank
    private String senderLastName;

    @NotNull
    private LocalDateTime timestamp;

    @ValidEmailOrAdmin
    private String recipientEmail;

    public ChatMessageDto(ChatMessage chatMessage, String name, String lastname) {
        this.content = chatMessage.getContent();
        this.senderEmail = chatMessage.getSenderEmail();
        this.timestamp = chatMessage.getTimestamp();
        this.recipientEmail = chatMessage.getRecipientEmail();
        this.senderFirstName = name;
        this.senderLastName = lastname;
    }

}