package inc.visor.voom.app.user.profile.dto;

public class ChangePasswordRequestDto {

    private String newPassword;
    private String confirmPassword;

    public ChangePasswordRequestDto() {
    }

    public ChangePasswordRequestDto(String newPassword, String confirmPassword) {
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
