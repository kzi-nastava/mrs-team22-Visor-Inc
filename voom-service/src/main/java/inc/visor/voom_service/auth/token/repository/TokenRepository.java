package inc.visor.voom_service.auth.token.repository;

import inc.visor.voom_service.auth.token.model.Token;
import inc.visor.voom_service.auth.token.model.TokenType;
import inc.visor.voom_service.auth.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token,String> {
  Optional<Token> findByTokenAndType(String token, TokenType type);
  List<Token> findAllByUserAndType(User user, TokenType type);
}
