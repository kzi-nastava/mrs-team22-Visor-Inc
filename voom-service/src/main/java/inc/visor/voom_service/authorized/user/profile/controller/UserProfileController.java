package inc.visor.voom_service.authorized.user.profile.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.authorized.user.profile.dto.ChangePasswordRequestDto;
import inc.visor.voom_service.authorized.user.profile.dto.UpdateUserProfileRequestDto;
import inc.visor.voom_service.authorized.user.profile.dto.UserProfileResponseDto;
import inc.visor.voom_service.authorized.user.profile.service.UserProfileService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users/me")
public class UserProfileController {
    
    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ResponseEntity<UserProfileResponseDto> getProfile(
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            UserProfileResponseDto dto = new UserProfileResponseDto(
                "test@example.com",
                "Test",
                "User",
                "+38160000000",
                "Test Address"
            );
            return ResponseEntity.ok(dto);
        }

        return ResponseEntity.ok(userProfileService.getProfile(user));
    }

    @PutMapping
    public ResponseEntity<UserProfileResponseDto> updateProfile(
        @Valid @RequestBody UpdateUserProfileRequestDto request,
        @AuthenticationPrincipal User user
    ) {

        if (user == null) {
            UserProfileResponseDto response = new UserProfileResponseDto(
                "test@example.com",
                request.getFirstName(),
                request.getLastName(),
                request.getPhoneNumber(),
                request.getAddress()
            );
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.ok(userProfileService.updateProfile(user, request));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
        @Valid @RequestBody ChangePasswordRequestDto request,
        @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            return ResponseEntity.ok().build();
        }

        userProfileService.changePassword(user, request);
        return ResponseEntity.ok().build();
    }

}
