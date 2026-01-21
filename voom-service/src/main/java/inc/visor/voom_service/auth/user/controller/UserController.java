package inc.visor.voom_service.auth.user.controller;

import inc.visor.voom_service.auth.user.dto.UserProfileDto;
import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.service.UserService;
import inc.visor.voom_service.exception.NotFoundException;
import inc.visor.voom_service.person.model.Person;
import inc.visor.voom_service.person.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController("/api/user")
public class UserController {

    private final UserService userService;
    private final PersonService personService;

    public UserController(UserService userService, PersonService personService) {
        this.userService = userService;
        this.personService = personService;
    }

    @GetMapping
    public ResponseEntity<List<UserProfileDto>> getUsers() {
        return ResponseEntity.ok(userService.getUsers().stream().map(UserProfileDto::new).toList());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDto> getUser(@PathVariable("userId") Long personId) {
        User user = this.userService.getUser(personId).orElseThrow(NotFoundException::new);
        return ResponseEntity.ok(new UserProfileDto(user));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileDto> updateUser(@PathVariable("userId") Long personId, UserProfileDto userProfileDto) {
        User user = this.userService.getUser(personId).orElseThrow(NotFoundException::new);
        Person person = new Person(user.getPerson().getId(), userProfileDto);
        this.personService.update(person);
        this.userService.update(user);
        return ResponseEntity.ok(userProfileDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long personId) {
        this.userService.getUser(personId).orElseThrow(NotFoundException::new);
        this.userService.deleteUser(personId);
        return ResponseEntity.ok().build();
    }

}
