package inc.visor.voom_service.authorized.user.profile.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.authorized.user.profile.dto.UserProfileResponseDto;
import inc.visor.voom_service.authorized.user.profile.service.UserProfileService;

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
        return ResponseEntity.ok(userProfileService.getProfile(user));
    }
}
