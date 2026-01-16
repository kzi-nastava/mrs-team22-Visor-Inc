package inc.visor.voom_service.auth.token.service;

import inc.visor.voom_service.auth.user.model.VoomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

  private final SecretKey signingKey;
  private final long expiration;
  private final long verificationExpiration;

  public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expiration, @Value("${jwt.verificationExpiration}") long verificationExpiration) {
    this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    this.expiration = expiration;
    this.verificationExpiration = verificationExpiration;
  }

  public long getExpiration() {
    return expiration;
  }

  public long getVerificationExpiration() {
    return verificationExpiration;
  }

  public String generateAccessToken(UserDetails userDetails, List<String> permissions) {
    return Jwts
            .builder()
            .subject(userDetails.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .claim("permissions", permissions)
            .claim("token_type", "Bearer")
            .signWith(signingKey)
            .compact();
  }

  public String generateRefreshToken(UserDetails userDetails) {
    return Jwts.builder()
            .subject(userDetails.getUsername())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .claim("token_type", "REFRESH")
            .signWith(signingKey)
            .compact();
  }

  public String generateEmailVerificationToken(UserDetails userDetails) {
    return Jwts.builder()
            .subject(userDetails.getUsername())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + verificationExpiration))
            .claim("token_type", "EMAIL_VERIFICATION")
            .signWith(signingKey)
            .compact();
  }

  public String generatePasswordResetToken(UserDetails userDetails) {
    return Jwts.builder()
            .subject(userDetails.getUsername())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + verificationExpiration))
            .claim("token_type", "PASSWORD_RESET")
            .signWith(signingKey)
            .compact();
  }

  public String extractUsername(String token) {
    return extractClaims(token).getSubject();
  }

  public Date extractExpiration(String token) {
    return extractClaims(token).getExpiration();
  }

  public String extractTokenType(String token) {
    return extractClaims(token).get("token_type", String.class);
  }

  public List<String> extractPermissions(String token) {
    return (List<String>) extractClaims(token).get("permissions",  List.class);
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  private boolean isTokenExpired(String token) {
    return extractClaims(token).getExpiration().before(new Date());
  }

  private Claims extractClaims(String token) {
    return Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
  }

}
