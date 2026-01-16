package inc.visor.voom_service.auth.service;

import inc.visor.voom_service.auth.dto.*;
import inc.visor.voom_service.auth.token.model.Token;
import inc.visor.voom_service.auth.token.model.TokenType;
import inc.visor.voom_service.auth.token.service.JwtService;
import inc.visor.voom_service.auth.token.service.TokenService;
import inc.visor.voom_service.auth.user.model.*;
import inc.visor.voom_service.auth.user.service.UserRoleService;
import inc.visor.voom_service.auth.user.service.UserService;
import inc.visor.voom_service.mail.EmailService;
import inc.visor.voom_service.person.model.Person;
import inc.visor.voom_service.person.service.PersonService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final PersonService personService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final UserRoleService userRoleService;
    private final JwtService jwtService;
    private final TokenService tokenService;

    public AuthService(UserService userService, PersonService personService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, EmailService emailService, UserRoleService userRoleService, JwtService jwtService, TokenService tokenService) {
        this.userService = userService;
        this.personService = personService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.userRoleService = userRoleService;
        this.jwtService = jwtService;
        this.tokenService = tokenService;
    }

    public TokenDto login(LoginDto dto) {
        User user = userService.readByEmail(dto.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("User is not active.");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );
        Token refreshToken = new Token(jwtService.generateRefreshToken(new VoomUserDetails(user)), TokenType.REFRESH, user, this.jwtService.getExpiration());
        Token accessToken = new Token(jwtService.generateAccessToken(new VoomUserDetails(user), user.getUserRole().getPermissions().stream().map(Permission::getAuthority).toList()), TokenType.ACCESS, user, this.jwtService.getExpiration());
        tokenService.create(refreshToken);
        tokenService.create(accessToken);
        return new TokenDto(user, refreshToken.getToken(), accessToken.getToken());
    }

    public User register(RegistrationDto dto) {
        Person person = this.personService.create(new Person(dto));
        UserRole userRole = this.userRoleService.read(dto.getUserType()).orElseThrow(() -> new RuntimeException("User role not found"));
        User user = this.userService.create(new User(dto.getEmail(), passwordEncoder.encode(dto.getPassword()), person, userRole));
        Token verificationToken = new Token(jwtService.generateEmailVerificationToken(new VoomUserDetails(user)), TokenType.EMAIL_VERIFICATION, user, this.jwtService.getVerificationExpiration());
        tokenService.create(verificationToken);
        sendVerificationEmail(user, verificationToken.getToken());
        return user;
    }

    public void verifyUser(String token) {
        String email = jwtService.extractUsername(token);
        User user = userService.readByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Token verificationToken = tokenService.readToken(user.getId(), TokenType.EMAIL_VERIFICATION).orElseThrow(() -> new RuntimeException("Verification token not found"));
        if (user.getUserStatus() == UserStatus.ACTIVE) {
            throw new RuntimeException("User is already active.");
        } else if (!verificationToken.getToken().equals(token)) {
            throw new RuntimeException("Invalid verification token.");
        } else {
            user.setUserStatus(UserStatus.ACTIVE);
            userService.update(user);
        }
    }

    public void forgotPassword(String email) {
        User user = userService.readByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Token resetToken = new Token(jwtService.generatePasswordResetToken(new VoomUserDetails(user)), TokenType.PASSWORD_RESET, user, this.jwtService.getVerificationExpiration());
        sendForgotPasswordEmail(user, resetToken.getToken());
    }

    public void resetPassword(ResetPasswordDto dto) {
        String email = jwtService.extractUsername(dto.getToken());
        User user = userService.readByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Token verificationToken = tokenService.readToken(user.getId(), TokenType.PASSWORD_RESET).orElseThrow(() -> new RuntimeException("Verification token not found"));
        if (!verificationToken.getToken().equals(dto.getToken())) {
            throw new RuntimeException("Invalid verification token.");
        } else {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            userService.update(user);
        }
    }

    public TokenDto refreshToken(String refreshToken) {
        String email = jwtService.extractUsername(refreshToken);
        User user = userService.readByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Token storedRefreshToken = tokenService.readToken(user.getId(), TokenType.REFRESH).orElseThrow(() -> new RuntimeException("Refresh token not found"));
        if (!storedRefreshToken.getToken().equals(refreshToken)) {
            throw new RuntimeException("Invalid refresh token.");
        }
        Token accessToken = new Token(jwtService.generateAccessToken(new VoomUserDetails(user), user.getUserRole().getPermissions().stream().map(Permission::getAuthority).toList()), TokenType.ACCESS, user, this.jwtService.getExpiration());
        return new TokenDto(user, refreshToken, accessToken.getToken());
    }

    public void sendVerificationEmail(User user, String token) {
        String subject = "Verify your email";
        String body = "Please verify your email by clicking the following link: " +
                "http://localhost:8080/verifyUser?token=" + token;
        emailService.send(user.getEmail(), subject, body);
    }

    public void sendForgotPasswordEmail(User user, String token) {
        String subject = "Reset your password";
        String body = "You can reset your password by clicking the following link: " +
                "http://localhost:4200/resetPassword?token=" + token;
        emailService.send(user.getEmail(), subject, body);
    };


}
