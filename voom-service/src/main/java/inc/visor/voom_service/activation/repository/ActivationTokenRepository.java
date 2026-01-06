package inc.visor.voom_service.activation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import inc.visor.voom_service.activation.model.ActivationToken;

public interface ActivationTokenRepository
        extends JpaRepository<ActivationToken, Long> {

    Optional<ActivationToken> findByToken(String token);
}
