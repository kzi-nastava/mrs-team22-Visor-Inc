package inc.visor.voom.app.user.profile;

import inc.visor.voom.app.user.profile.dto.UserProfileDto;
import retrofit2.Call;
import retrofit2.http.GET;
public interface ProfileApi {

    @GET("api/users/me")
    Call<UserProfileDto> getProfile();

}
