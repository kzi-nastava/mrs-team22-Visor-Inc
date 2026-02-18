package inc.visor.voom_service.chat.model;

import inc.visor.voom_service.chat.validations.ValidEmailOrAdmin;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Entity
@Data
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ValidEmailOrAdmin
    private String senderEmail;

    @ValidEmailOrAdmin
    private String recipientEmail;

    @NotBlank
    @Size(min = 1)
    private String content;

    private LocalDateTime timestamp;
}
