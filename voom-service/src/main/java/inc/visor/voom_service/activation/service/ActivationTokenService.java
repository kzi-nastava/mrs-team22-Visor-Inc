package inc.visor.voom_service.activation.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import inc.visor.voom_service.activation.model.ActivationToken;
import inc.visor.voom_service.activation.repository.ActivationTokenRepository;
import inc.visor.voom_service.auth.user.model.User;

@Service
public class ActivationTokenService {

    private final ActivationTokenRepository repository;

    public ActivationTokenService(ActivationTokenRepository repository) {
        this.repository = repository;
    }

    public ActivationToken createForUser(User user) {

        ActivationToken token = new ActivationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        token.setUser(user);
        token.setUsed(false);

        return repository.save(token);
    }

    public ActivationToken validate(String rawToken) {

        ActivationToken token = repository.findByToken(rawToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid activation token"));

        if (token.isUsed()) {
            throw new IllegalStateException("Activation token already used");
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Activation token expired");
        }

        return token;
    }

    public void markAsUsed(ActivationToken token) {
        token.setUsed(true);
        repository.save(token);
    }
}
