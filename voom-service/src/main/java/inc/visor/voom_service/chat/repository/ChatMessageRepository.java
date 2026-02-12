package inc.visor.voom_service.chat.repository;

import inc.visor.voom_service.chat.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderEmailOrRecipientEmailOrderByTimestampAsc(String email, String email2);

    @Query("SELECT DISTINCT CASE WHEN m.senderEmail = 'admin' THEN m.recipientEmail ELSE m.senderEmail END " +
            "FROM ChatMessage m WHERE m.senderEmail = 'admin' OR m.recipientEmail = 'admin'")
    List<String> findDistinctChatPartners();


    @Query("SELECT m FROM ChatMessage m WHERE " +
            "(m.senderEmail = :user1 AND m.recipientEmail = :user2) OR " +
            "(m.senderEmail = :user2 AND m.recipientEmail = :user1) " +
            "ORDER BY m.timestamp ASC")
    List<ChatMessage> findConversationHistory(String user1, String user2);
}
