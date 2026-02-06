package inc.visor.voom_service.shared.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import inc.visor.voom_service.shared.notification.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    Long countByUserIdAndReadFalse(Long userId);

    List<Notification> findByUserIdAndReadFalse(Long userId);
}
