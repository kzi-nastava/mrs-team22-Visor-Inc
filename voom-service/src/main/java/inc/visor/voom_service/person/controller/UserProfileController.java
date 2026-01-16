package inc.visor.voom_service.person.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.model.UserRole;
import inc.visor.voom_service.auth.user.model.UserStatus;
import inc.visor.voom_service.person.dto.ChangePasswordRequestDto;
import inc.visor.voom_service.person.dto.UpdateUserProfileRequestDto;
import inc.visor.voom_service.person.dto.UserProfileResponseDto;
import inc.visor.voom_service.person.model.Person;
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
            @AuthenticationPrincipal User user
    ) {

        Long userId = (user != null) ? user.getId() : 2L;

        return ResponseEntity.ok(userProfileService.getProfile(userId));
    }

    @PutMapping
    public ResponseEntity<UserProfileResponseDto> updateProfile(
        @Valid @RequestBody UpdateUserProfileRequestDto request,
        @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            
            Person person = new Person();
            person.setId(2L);

            UserType userType = new UserType();
            userType.setId(1);

            UserRole userRole = new UserRole();
            userRole.setId(1);

            User mockUser = new User(
                "nikola@test.com",
                "akjsdks",
                userType,
                UserStatus.ACTIVE, 
                userRole,
                person
            );

            return ResponseEntity.ok(userProfileService.updateProfile(mockUser, request));
        }
        
        return ResponseEntity.ok(userProfileService.updateProfile(user, request));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
        @Valid @RequestBody ChangePasswordRequestDto request,
        @AuthenticationPrincipal User user
    ) {
        Long userId = (user != null) ? user.getId() : 2L;

        userProfileService.changePassword(userId, request);

        return ResponseEntity.ok().build();
    }


}
