package inc.visor.voom_service.shared.notification.repository;

import inc.visor.voom_service.shared.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    Long countByUserIdAndReadFalse(Long userId);

    List<Notification> findByUserIdAndReadFalse(Long userId);
}
