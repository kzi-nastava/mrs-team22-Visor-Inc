package inc.visor.voom.app.user.profile;

import inc.visor.voom.app.user.profile.dto.ChangePasswordRequestDto;
import inc.visor.voom.app.user.profile.dto.UpdateUserProfileRequestDto;
import inc.visor.voom.app.user.profile.dto.UserProfileDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ProfileApi {

    @GET("api/users/me")
    Call<UserProfileDto> getProfile();

    @PUT("api/users/me")
    Call<UserProfileDto> updateProfile(@Body UpdateUserProfileRequestDto body);

    @PUT("api/users/me/password")
    Call<Void> changePassword(@Body ChangePasswordRequestDto body);
}
