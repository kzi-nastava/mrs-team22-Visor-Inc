package inc.visor.voom.app.driver.activate.dto;

public class ActivateDriverRequestDto {

    private String token;
    private String password;
    private String confirmPassword;

    public ActivateDriverRequestDto(String token, String password, String confirmPassword) {
        this.token = token;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getToken() { return token; }
    public String getPassword() { return password; }
    public String getConfirmPassword() { return confirmPassword; }
}
