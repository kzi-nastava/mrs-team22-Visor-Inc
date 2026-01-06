package inc.visor.voom_service.auth.token.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import inc.visor.voom_service.auth.token.model.Token;
import inc.visor.voom_service.auth.token.model.TokenType;
import inc.visor.voom_service.auth.user.model.User;

@Repository
public interface TokenRepository extends JpaRepository<Token,String> {
  Optional<Token> findByTokenAndTokenType(String token, TokenType type);
  List<Token> findTokensByTokenAndTokenType(User user, TokenType type);
}
