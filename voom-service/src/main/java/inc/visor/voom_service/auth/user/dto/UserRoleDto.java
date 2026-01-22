package inc.visor.voom_service.auth.user.dto;

import inc.visor.voom_service.auth.user.model.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRoleDto {
    private int id;
    private String roleName;

    public UserRoleDto(UserRole userRole) {
        this.id = userRole.getId();
        this.roleName = userRole.getRoleName();
    }
}
