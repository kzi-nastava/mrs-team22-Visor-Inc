package inc.visor.voom.app.shared.api;

import inc.visor.voom.app.shared.dto.authentication.ForgotPasswordDto;
import inc.visor.voom.app.shared.dto.authentication.LoginDto;
import inc.visor.voom.app.shared.dto.authentication.RegistrationDto;
import inc.visor.voom.app.shared.dto.authentication.ResetPasswordDto;
import inc.visor.voom.app.shared.dto.authentication.TokenDto;
import inc.visor.voom.app.shared.dto.authentication.UserDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthenticationApi {

    @POST("/api/auth/login")
    Call<TokenDto> login(@Body LoginDto dto);

    @POST("/api/auth/register")
    Call<UserDto> register(@Body RegistrationDto dto);

    @POST("/api/auth/refreshToken")
    Call<TokenDto> refreshToken(@Body String refreshToken);

    @POST("/api/auth/verifyUser")
    Call<Void> verifyUser(@Body String token);

    @POST("/api/auth/forgotPassword")
    Call<Void> forgotPassword(@Body ForgotPasswordDto dto);

    @POST("/api/auth/resetPassword")
    Call<Void> resetPassword(@Body ResetPasswordDto dto);


}
