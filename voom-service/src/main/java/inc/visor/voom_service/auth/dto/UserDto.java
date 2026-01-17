package inc.visor.voom_service.auth.dto;

import inc.visor.voom_service.auth.user.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;

    private UserDto() {
    }

    public UserDto(User user) {
        this.id = user.getId();
        this.firstName = user.getPerson().getFirstName();
        this.lastName = user.getPerson().getLastName();
        this.email = user.getEmail();
        this.role = user.getUserRole().getRoleName();
    }

}
