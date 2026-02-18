package inc.visor.voom_service.chat.controller;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.repository.UserRepository;
import inc.visor.voom_service.chat.dto.ChatMessageDto;
import inc.visor.voom_service.chat.dto.UserChatDto;
import inc.visor.voom_service.chat.model.ChatMessage;
import inc.visor.voom_service.chat.repository.ChatMessageRepository;
import inc.visor.voom_service.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository repository;
    private final ChatService chatService;
    private final UserRepository userRepository;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());
        ChatMessage saved = repository.save(chatMessage);

        User sender = userRepository.findByEmail(chatMessage.getSenderEmail()).orElse(null);

        ChatMessageDto dto;
        if (sender != null) {
            dto = new ChatMessageDto(saved, sender.getPerson().getFirstName(), sender.getPerson().getLastName());
        } else {
            dto = new ChatMessageDto(saved, "Unknown", "Unknown");
        }

        messagingTemplate.convertAndSend("/topic/messages/" + dto.getRecipientEmail(), dto);
        messagingTemplate.convertAndSend("/topic/messages/" + dto.getSenderEmail(), dto);
    }

    @GetMapping("/api/chat/conversations")
    public List<UserChatDto> getActiveConversations() {
        return chatService.findAllChatPartnersForAdmin().stream().map(UserChatDto::new).toList();
    }

    @GetMapping("/api/chat/history/{email}")
    public ResponseEntity<List<ChatMessageDto>> getChatHistory(@PathVariable String email) {
        return ResponseEntity.ok(repository.findBySenderEmailOrRecipientEmailOrderByTimestampAsc(email, email).stream().map(cm -> {
            User sender = userRepository.findByEmail(cm.getSenderEmail()).orElse(null);
            ChatMessageDto dto;
            if (sender != null) {
                dto = new ChatMessageDto(cm, sender.getPerson().getFirstName(), sender.getPerson().getLastName());
            } else {
                dto = new ChatMessageDto(cm, "Unknown", "Unknown");
            }
            return dto;
        }).toList());
    }

    @GetMapping("/api/chat/history/{userEmail}/{partnerEmail}")
    public ResponseEntity<List<ChatMessageDto>> getChatHistory(
            @PathVariable String userEmail,
            @PathVariable String partnerEmail) {

        return ResponseEntity.ok(repository.findConversationHistory(userEmail, partnerEmail).stream().map(cm -> {
            User sender = userRepository.findByEmail(cm.getSenderEmail()).orElse(null);
            ChatMessageDto dto;
            if (sender != null) {
                dto = new ChatMessageDto(cm, sender.getPerson().getFirstName(), sender.getPerson().getLastName());
            } else {
                dto = new ChatMessageDto(cm, "Unknown", "Unknown");
            }
            return dto;
        }).toList());
    }
}
