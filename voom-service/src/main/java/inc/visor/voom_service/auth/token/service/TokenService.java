package inc.visor.voom_service.auth.token.service;

import inc.visor.voom_service.auth.token.model.Token;
import inc.visor.voom_service.auth.token.model.TokenType;
import inc.visor.voom_service.auth.token.repository.TokenRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public Token create(Token token) {
        return tokenRepository.save(token);
    }

    public Token update(Token token) {
        return tokenRepository.save(token);
    }

    public Optional<Token> readToken(Long userId, TokenType tokenType) {
        return tokenRepository.find(userId, tokenType);
    }

}
