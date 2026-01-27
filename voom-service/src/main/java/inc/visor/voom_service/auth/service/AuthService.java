package inc.visor.voom_service.auth.service;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import inc.visor.voom_service.auth.dto.LoginDto;
import inc.visor.voom_service.auth.dto.RegistrationDto;
import inc.visor.voom_service.auth.dto.ResetPasswordDto;
import inc.visor.voom_service.auth.dto.TokenDto;
import inc.visor.voom_service.auth.token.model.Token;
import inc.visor.voom_service.auth.token.model.TokenType;
import inc.visor.voom_service.auth.token.service.JwtService;
import inc.visor.voom_service.auth.token.service.TokenService;
import inc.visor.voom_service.auth.user.model.Permission;
import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.model.UserRole;
import inc.visor.voom_service.auth.user.model.UserStatus;
import inc.visor.voom_service.auth.user.model.VoomUserDetails;
import inc.visor.voom_service.auth.user.service.UserRoleService;
import inc.visor.voom_service.auth.user.service.UserService;
import inc.visor.voom_service.driver.model.Driver;
import inc.visor.voom_service.driver.service.DriverService;
import inc.visor.voom_service.exception.NotFoundException;
import inc.visor.voom_service.mail.EmailService;
import inc.visor.voom_service.person.model.Person;
import inc.visor.voom_service.person.service.PersonService;
import inc.visor.voom_service.simulation.Simulator;

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
    private final Simulator simulatorService;
    private final DriverService driverService;

    public AuthService(UserService userService, PersonService personService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, EmailService emailService, UserRoleService userRoleService, JwtService jwtService, TokenService tokenService, Simulator simulatorService, DriverService driverService) {
        this.userService = userService;
        this.personService = personService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.userRoleService = userRoleService;
        this.jwtService = jwtService;
        this.tokenService = tokenService;
        this.simulatorService = simulatorService;
        this.driverService = driverService;
    }

    public TokenDto login(LoginDto dto) {
        User user = userService.getUser(dto.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("User is not active.");
        }

        if (user.getUserRole().getId() == 2) {
            System.out.println("Adding driver to simulation: " + user.getId());
            Driver driver = driverService.getDriverFromUser(user.getId()).orElseThrow(NotFoundException::new);
            simulatorService.addActiveDriver(driver.getId());
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );

        String refreshTokenText = jwtService.generateRefreshToken(new VoomUserDetails(user));
        String accessTokenText = jwtService.generateAccessToken(new VoomUserDetails(user), user.getUserRole().getPermissions().stream().map(Permission::getAuthority).toList());

        Token refreshToken = tokenService.getToken(user, TokenType.REFRESH).orElse(null);
        Token accessToken = tokenService.getToken(user, TokenType.ACCESS).orElse(null);

        if (refreshToken != null) {
            refreshToken.setToken(refreshTokenText);
            refreshToken.setExpiryDateTime(LocalDateTime.ofInstant(jwtService.extractExpiration(refreshTokenText).toInstant(), java.time.ZoneId.systemDefault()));
            tokenService.update(refreshToken);
        } else {
            refreshToken = new Token(refreshTokenText, TokenType.REFRESH, user, jwtService.extractExpiration(refreshTokenText).getTime());
            tokenService.create(refreshToken);
        }

        accessToken = getAccessToken(user, accessTokenText, accessToken);

        return new TokenDto(user, refreshTokenText, accessToken.getToken());
    }

    private Token getAccessToken(User user, String accessTokenText, Token accessToken) {
        if (accessToken != null) {
            accessToken.setToken(accessTokenText);
            accessToken.setExpiryDateTime(LocalDateTime.ofInstant(jwtService.extractExpiration(accessTokenText).toInstant(), java.time.ZoneId.systemDefault()));
            tokenService.update(accessToken);
        } else {
            accessToken = new Token(accessTokenText, TokenType.ACCESS, user, jwtService.extractExpiration(accessTokenText).getTime());
            tokenService.create(accessToken);
        }
        return accessToken;
    }

    public User register(RegistrationDto dto) {
        Person person = this.personService.create(new Person(dto));
        UserRole userRole = this.userRoleService.getUserRole(dto.getUserType()).orElseThrow(() -> new RuntimeException("User role not found"));
        User user = this.userService.create(new User(dto.getEmail(), passwordEncoder.encode(dto.getPassword()), person, userRole));
        String verificationTokenText = jwtService.generateEmailVerificationToken(new VoomUserDetails(user));
        Token verificationToken = tokenService.getToken(user, TokenType.EMAIL_VERIFICATION).orElse(null);
        if (verificationToken != null) {
            verificationToken.setToken(verificationTokenText);
            verificationToken.setExpiryDateTime(LocalDateTime.ofInstant(jwtService.extractExpiration(verificationTokenText).toInstant(), java.time.ZoneId.systemDefault()));
            tokenService.update(verificationToken);
        } else {
            verificationToken = new Token(verificationTokenText, TokenType.EMAIL_VERIFICATION, user, this.jwtService.extractExpiration(verificationTokenText).getTime());
            tokenService.create(verificationToken);
        }
        sendVerificationEmail(user, verificationTokenText);
        return user;
    }

    public void verifyUser(String token) {
        String email = jwtService.extractUsername(token);
        User user = userService.getUser(email).orElseThrow(() -> new RuntimeException("User not found"));
        Token verificationToken = tokenService.getToken(user, TokenType.EMAIL_VERIFICATION).orElseThrow(() -> new RuntimeException("Verification token not found"));
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
        User user = userService.getUser(email).orElseThrow(() -> new RuntimeException("User not found"));
        String passwordResetTokenText = jwtService.generatePasswordResetToken(new VoomUserDetails(user));
        Token passwordResetToken = tokenService.getToken(user, TokenType.PASSWORD_RESET).orElse(null);
        if (passwordResetToken != null) {
            passwordResetToken.setToken(passwordResetTokenText);
            passwordResetToken.setExpiryDateTime(LocalDateTime.ofInstant(jwtService.extractExpiration(passwordResetTokenText).toInstant(), java.time.ZoneId.systemDefault()));
            tokenService.update(passwordResetToken);
        } else {
            passwordResetToken = new Token(passwordResetTokenText, TokenType.PASSWORD_RESET, user, this.jwtService.extractExpiration(passwordResetTokenText).getTime());
            tokenService.create(passwordResetToken);
        }
        sendForgotPasswordEmail(user, passwordResetTokenText);
    }

    public void resetPassword(ResetPasswordDto dto) {
        String email = jwtService.extractUsername(dto.getToken());
        User user = userService.getUser(email).orElseThrow(() -> new RuntimeException("User not found"));
        Token verificationToken = tokenService.getToken(user, TokenType.PASSWORD_RESET).orElseThrow(() -> new RuntimeException("Verification token not found"));
        if (!verificationToken.getToken().equals(dto.getToken())) {
            throw new RuntimeException("Invalid verification token.");
        } else {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            userService.update(user);
        }
    }

    public TokenDto refreshToken(String refreshToken) {
        String email = jwtService.extractUsername(refreshToken);
        User user = userService.getUser(email).orElseThrow(() -> new RuntimeException("User not found"));
        Token storedRefreshToken = tokenService.getToken(user, TokenType.REFRESH).orElseThrow(() -> new RuntimeException("Refresh token not found"));
        if (!storedRefreshToken.getToken().equals(refreshToken)) {
            throw new RuntimeException("Invalid refresh token.");
        }

        String accessTokenText = jwtService.generateAccessToken(new VoomUserDetails(user), user.getUserRole().getPermissions().stream().map(Permission::getAuthority).toList());
        Token accessToken = tokenService.getToken(user, TokenType.ACCESS).orElse(null);

        getAccessToken(user, accessTokenText, accessToken);

        return new TokenDto(user, refreshToken, accessTokenText);
    }

    public void sendVerificationEmail(User user, String token) {
        String subject = "Verify your email";
        String body = "Please verify your email by clicking the following link: http://localhost:4200/voom/verifyUser?token=" + token;
        emailService.send(user.getEmail(), subject, body);
    }

    public void sendForgotPasswordEmail(User user, String token) {
        String subject = "Reset your password";
        String body = "You can reset your password by clicking the following link: http://localhost:4200/voom/resetPassword?token=" + token;
        emailService.send(user.getEmail(), subject, body);
    }
}
