package inc.visor.voom_service.auth.user.dto;

import inc.visor.voom_service.auth.user.model.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRoleDto {

    @NotNull(message = "User role id must not be null")
    private int id;

    @NotBlank(message = "User role name must not be null")
    private String roleName;

    public UserRoleDto() {
    }

    public UserRoleDto(UserRole userRole) {
        this.id = userRole.getId();
        this.roleName = userRole.getRoleName();
    }
}
