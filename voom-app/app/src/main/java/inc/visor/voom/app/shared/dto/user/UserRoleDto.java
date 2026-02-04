package inc.visor.voom.app.shared.dto.user;


public class UserRoleDto {
    private int id;
    private String roleName;

    public UserRoleDto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
