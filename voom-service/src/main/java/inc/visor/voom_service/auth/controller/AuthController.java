package inc.visor.voom_service.auth.controller;

import inc.visor.voom_service.auth.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import inc.visor.voom_service.auth.service.AuthService;
import inc.visor.voom_service.auth.token.service.JwtService;
import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    AuthService authService;
    JwtService jwtService;
    UserService userService;

    public AuthController(AuthService authService, JwtService jwtService, UserService userService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginDto dto) {
        TokenDto tokenDto = authService.login(dto);
        return ResponseEntity.ok().body(tokenDto);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegistrationDto registrationDto) {
        User user = authService.register(registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserDto(user));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<TokenDto> refreshToken(@NotBlank(message = "Token is required") @RequestBody String refreshToken) {
        TokenDto tokenDto = authService.refreshToken(refreshToken);
        return ResponseEntity.ok().body(tokenDto);
    }

    @PostMapping("/verifyUser")
    public ResponseEntity<Void> verifyUser(@RequestBody VerifyTokenDto token) {
        authService.verifyUser(token.getToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordDto dto) {
        authService.forgotPassword(dto.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordDto dto) {
        authService.resetPassword(dto);
        return ResponseEntity.noContent().build();
    }

}
