package inc.visor.voom_service.auth.user.controller;

import inc.visor.voom_service.auth.user.dto.BlockUserRequestDto;
import inc.visor.voom_service.auth.user.dto.CreateUserDto;
import inc.visor.voom_service.auth.user.dto.UserBlockNoteDto;
import inc.visor.voom_service.auth.user.dto.UserProfileDto;
import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.model.UserRole;
import inc.visor.voom_service.auth.user.model.VoomUserDetails;
import inc.visor.voom_service.auth.user.service.UserBlockService;
import inc.visor.voom_service.auth.user.service.UserRoleService;
import inc.visor.voom_service.auth.user.service.UserService;
import inc.visor.voom_service.exception.NotFoundException;
import inc.visor.voom_service.person.model.Person;
import inc.visor.voom_service.person.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PersonService personService;
    private final UserRoleService userRoleService;
    private final UserBlockService userBlockService;

    public UserController(UserService userService, PersonService personService, UserRoleService userRoleService, UserBlockService userBlockService) {
        this.userService = userService;
        this.personService = personService;
        this.userRoleService = userRoleService;
        this.userBlockService = userBlockService;
    }

    @GetMapping
    public ResponseEntity<List<UserProfileDto>> getUsers() {
        return ResponseEntity.ok(userService.getUsers().stream().map(UserProfileDto::new).toList());
    }

    @GetMapping("{userId}")
    public ResponseEntity<UserProfileDto> getUser(@PathVariable("userId") Long personId) {
        User user = this.userService.getUser(personId).orElseThrow(NotFoundException::new);
        return ResponseEntity.ok(new UserProfileDto(user));
    }

    @PostMapping
    public ResponseEntity<UserProfileDto> createUser(@Valid @RequestBody CreateUserDto dto) {
        Person person = new Person(dto);
        person = this.personService.create(person);
        UserRole userRole = userRoleService.getUserRole(dto.getUserRoleId()).orElseThrow(NotFoundException::new);
        User user = new User(dto, person, userRole);
        user = this.userService.create(user);
        return ResponseEntity.ok(new UserProfileDto(user));
    }

    @PutMapping("{userId}")
    public ResponseEntity<UserProfileDto> updateUser(@PathVariable("userId") Long userId, @Valid @RequestBody UserProfileDto dto) {
        User user = this.userService.getUser(userId).orElseThrow(NotFoundException::new);
        UserRole userRole = this.userRoleService.getUserRole(dto.getUserRoleId()).orElseThrow(NotFoundException::new);
        Person person = new Person(user.getPerson().getId(), dto);
        person = this.personService.update(person);
        user = new User(person, dto, userRole);
        user = this.userService.update(user);
        return ResponseEntity.ok(new UserProfileDto(user));
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long personId) {
        this.userService.getUser(personId).orElseThrow(NotFoundException::new);
        this.userService.deleteUser(personId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{userId}/block")
    public ResponseEntity<UserProfileDto> blockUser(
            @AuthenticationPrincipal VoomUserDetails userDetails,
            @PathVariable("userId") Long userId,
            @Valid @RequestBody BlockUserRequestDto dto
    ) {

        String username = userDetails != null ? userDetails.getUsername() : null;

        if (username == null) {
            return ResponseEntity.status(403).build();
        }

        User admin = userService.getUser(username).orElseThrow(NotFoundException::new);

        User user = userBlockService.blockUser(userId, admin.getId(), dto);

        return ResponseEntity.ok(new UserProfileDto(user));
    }

    @PostMapping("{userId}/unblock")
    public ResponseEntity<UserProfileDto> unblockUser(
            @PathVariable("userId") Long userId
    ) {

        User user = userBlockService.unblockUser(userId);

        return ResponseEntity.ok(new UserProfileDto(user));
    }

    @GetMapping("{userId}/block-note")
    public ResponseEntity<UserBlockNoteDto> getActiveBlockNote(
            @PathVariable("userId") Long userId
    ) {

        return userBlockService.getActiveBlockNote(userId)
                .map(note -> ResponseEntity.ok(new UserBlockNoteDto(note)))
                .orElse(ResponseEntity.notFound().build());
    }

}
