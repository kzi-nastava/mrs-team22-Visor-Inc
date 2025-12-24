package inc.visor.voom_service.auth.token.model;

import inc.visor.voom_service.auth.user.model.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getExpiryDateTime() {
        return expiryDateTime;
    }

    public void setExpiryDateTime(LocalDateTime expiryDateTime) {
        this.expiryDateTime = expiryDateTime;
    }

    @Override
    public String toString() {
        return "EmailVerificationToken{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", user=" + user +
                ", expiryDateTime=" + expiryDateTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EmailVerificationToken that = (EmailVerificationToken) o;
        return Objects.equals(id, that.id) && Objects.equals(token, that.token) && Objects.equals(user, that.user) && Objects.equals(expiryDateTime, that.expiryDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, token, user, expiryDateTime);
    }
}
