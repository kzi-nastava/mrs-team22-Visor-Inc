package inc.visor.voom_service.shared.notification.model;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.shared.notification.model.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "ride_id")
    private Long rideId;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Notification() {
    }

    public Notification(User user,
                        NotificationType type,
                        String title,
                        String message,
                        Long rideId) {
        this.user = user;
        this.type = type;
        this.title = title;
        this.message = message;
        this.rideId = rideId;
        this.read = false;
        this.createdAt = LocalDateTime.now();
    }
}
