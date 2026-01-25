package inc.visor.voom_service.auth.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String userStatus;
    private String email;
    private String phoneNumber;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime birthDate;
    private String address;
    private int userRoleId;

    public UserProfileDto() {}

    public UserProfileDto(User user) {
        this.id = user.getId();
        this.firstName = user.getPerson().getFirstName();
        this.lastName = user.getPerson().getLastName();
        this.pfpUrl = null;
        this.userStatus = user.getUserStatus().toString();
        this.email = user.getEmail();
        this.phoneNumber = user.getPerson().getPhoneNumber();
        this.birthDate = user.getPerson().getBirthDate();
        this.address = user.getPerson().getAddress();
        this.userRoleId = user.getUserRole().getId();
    }

}
