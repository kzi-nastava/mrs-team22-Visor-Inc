package inc.visor.voom_service.shared.notification.service;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.shared.notification.model.Notification;
import inc.visor.voom_service.shared.notification.model.enums.NotificationType;
import inc.visor.voom_service.shared.notification.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public Notification createAndSendNotification(
            User user,
            NotificationType type,
            String title,
            String message,
            Long rideId
    ) {

        Notification notification =
                new Notification(user, type, title, message, rideId);

        Notification saved = notificationRepository.save(notification);

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + user.getId(),
                saved
        );

        return saved;
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        System.out.println("Marking notification as read: " + notificationId);
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public List<Notification> getUnreadForUser(Long userId) {
        return notificationRepository.findByUserIdAndReadFalse(userId);
    }
}
