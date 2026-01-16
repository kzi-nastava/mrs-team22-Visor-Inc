package inc.visor.voom_service.auth.dto;

import inc.visor.voom_service.auth.user.model.User;

public class TokenDto {

    private UserDto user;
    private String refreshToken;
    private String accessToken;

    public TokenDto() {
    }

    public TokenDto(User user, String refreshToken, String accessToken) {
        this.user = new UserDto(user);
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}

