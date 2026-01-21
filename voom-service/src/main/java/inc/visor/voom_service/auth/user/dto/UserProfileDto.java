package inc.visor.voom_service.auth.user.dto;

import inc.visor.voom_service.auth.user.model.User;
import inc.visor.voom_service.auth.user.model.UserStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserProfileDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String pfpUrl;
    private UserStatus userStatus;
    private String email;
    private String phoneNumber;
    private LocalDateTime birthDate;
    private String address;

    public UserProfileDto(User user) {

    }

}
