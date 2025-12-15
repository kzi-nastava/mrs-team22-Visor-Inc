package inc.visor.voom_service.auth.token;

import inc.visor.voom_service.auth.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification_token")
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verification_token_id", nullable = false)
    private Long id;

    @Column(name = "verification_token", nullable = false, unique = true)
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expiry_date_time", nullable = false)
    private LocalDateTime expiryDateTime;

}
