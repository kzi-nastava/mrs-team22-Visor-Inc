package inc.visor.voom_service.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import inc.visor.voom_service.auth.dto.ForgotPasswordDto;
import inc.visor.voom_service.auth.dto.LoginDto;
import inc.visor.voom_service.auth.dto.TokenDto;
import inc.visor.voom_service.auth.dto.RegistrationDto;
import inc.visor.voom_service.auth.dto.ResetPasswordDto;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody LoginDto loginDto) {
        TokenDto tokenDto = new TokenDto();
        return ResponseEntity.ok().body(tokenDto);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegistrationDto registrationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody TokenDto refreshTokenDto) {
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<TokenDto> refreshToken(@RequestBody TokenDto refreshTokenDto) {
        TokenDto tokenDto = new TokenDto();
        return ResponseEntity.ok().body(tokenDto);
    }

    @GetMapping("/verifyEmail")
    public ResponseEntity<Void> verifyEmail(@RequestParam("token") String token) {
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordDto forgotPasswordRequestDto) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordDto resetPasswordRequestDto) {
        return ResponseEntity.noContent().build();
    }

}
