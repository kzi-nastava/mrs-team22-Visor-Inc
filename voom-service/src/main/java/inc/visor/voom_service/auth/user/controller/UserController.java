package inc.visor.voom_service.auth.user.controller;

import inc.visor.voom_service.auth.user.dto.UserProfileDto;
import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.model.UserRole;
import inc.visor.voom_service.auth.user.model.UserStatus;
import inc.visor.voom_service.auth.user.repository.UserRoleRepository;
import inc.visor.voom_service.auth.user.service.UserRoleService;
import inc.visor.voom_service.auth.user.service.UserService;
import inc.visor.voom_service.exception.NotFoundException;
import inc.visor.voom_service.person.model.Person;
import inc.visor.voom_service.person.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PersonService personService;
    private final UserRoleService userRoleService;

    public UserController(UserService userService, PersonService personService, UserRoleService userRoleService) {
        this.userService = userService;
        this.personService = personService;
        this.userRoleService = userRoleService;
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

    //TODO add createUser;

    @PutMapping("{userId}")
    public ResponseEntity<UserProfileDto> updateUser(@PathVariable("userId") Long personId, @RequestBody UserProfileDto dto) {
        User user = this.userService.getUser(personId).orElseThrow(NotFoundException::new);
        UserRole userRole = this.userRoleService.getUserRole(dto.getUserRoleId()).orElseThrow(NotFoundException::new);
        Person person = new Person(user.getPerson().getId(), dto);
        person = this.personService.update(person);
        user = new User(personId, person, UserStatus.valueOf(dto.getUserStatus()), userRole);
        user = this.userService.update(user);
        return ResponseEntity.ok(new UserProfileDto(user));
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long personId) {
        this.userService.getUser(personId).orElseThrow(NotFoundException::new);
        this.userService.deleteUser(personId);
        return ResponseEntity.noContent().build();
    }

}
