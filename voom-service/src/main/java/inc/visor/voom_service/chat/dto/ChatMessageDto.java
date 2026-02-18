package inc.visor.voom_service.chat.dto;

import inc.visor.voom_service.chat.model.ChatMessage;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageDto {
    private String content;
    private String senderEmail;
    //    private UserChatDto sender;
    private String senderFirstName;
    private String senderLastName;
    private LocalDateTime timestamp;
    private String recipientEmail;

    public ChatMessageDto(ChatMessage chatMessage, String name, String lastname) {
        this.content = chatMessage.getContent();
        this.senderEmail = chatMessage.getSenderEmail();
        this.timestamp = chatMessage.getTimestamp();
        this.recipientEmail = chatMessage.getRecipientEmail();
        this.senderFirstName = name;
        this.senderLastName = lastname;
    }

//    public ChatMessageDto(ChatMessage chatMessage) {
//        this.content = chatMessage.getContent();
//        this.senderEmail = chatMessage.getSenderEmail();
//        this.timestamp = chatMessage.getTimestamp();
//        this.recipientEmail = chatMessage.getRecipientEmail();
//        this.senderFirstName = "";
//        this.senderLastName = "";
//    }
}