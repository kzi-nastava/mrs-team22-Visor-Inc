package inc.visor.voom_service.person.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.service.UserService;
import inc.visor.voom_service.person.dto.ChangePasswordRequestDto;
import inc.visor.voom_service.person.dto.UpdateUserProfileRequestDto;
import inc.visor.voom_service.person.dto.UserProfileResponseDto;
import inc.visor.voom_service.person.model.Person;
import inc.visor.voom_service.person.service.UserProfileService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users/me")
public class UserProfileController {

    private final UserService userService;
    
    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService, UserService userService) {
        this.userProfileService = userProfileService;
        this.userService = userService;
    }

    Person dummyPerson = new Person("Nikola", "Bjelica", "1234567890", "Tose Jovanovica 57a, Novi Sad");
    User dummyUser = new User("nikolabjelica4@gmail.com", "password", null, null, null, dummyPerson);

    @GetMapping
    public ResponseEntity<UserProfileResponseDto> getProfile(
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            return ResponseEntity.ok(
                userProfileService.getProfile(dummyUser)
            );
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
