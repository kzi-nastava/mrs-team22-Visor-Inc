package inc.visor.voom_service.person.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.model.VoomUserDetails;
import inc.visor.voom_service.person.dto.ChangePasswordRequestDto;
import inc.visor.voom_service.person.dto.UpdateUserProfileRequestDto;
import inc.visor.voom_service.person.dto.UserProfileResponseDto;
import inc.visor.voom_service.person.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/users/me")
public class UserProfileController {
    
    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ResponseEntity<UserProfileResponseDto> getProfile(
            @AuthenticationPrincipal VoomUserDetails userDetails
    ) {
        String username = userDetails.getUsername();
        User user = userProfileService.getUserByEmail(username);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userProfileService.getProfile(user.getId()));
    }

    @PutMapping
    public ResponseEntity<UserProfileResponseDto> updateProfile(
        @Valid @RequestBody UpdateUserProfileRequestDto request,
        @AuthenticationPrincipal VoomUserDetails userDetails
    ) {
        String username = userDetails.getUsername();
        User user = userProfileService.getUserByEmail(username);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(userProfileService.updateProfile(user, request));
    }

    //FIXME what
    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
        @Valid @RequestBody ChangePasswordRequestDto request,
        @AuthenticationPrincipal VoomUserDetails userDetails
    ) {
        String username = userDetails.getUsername();
        User user = userProfileService.getUserByEmail(username);
        
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        userProfileService.changePassword(user.getId(), request);

        return ResponseEntity.ok().build();
    }


}
