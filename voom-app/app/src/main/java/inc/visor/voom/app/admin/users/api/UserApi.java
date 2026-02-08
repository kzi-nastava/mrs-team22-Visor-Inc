package inc.visor.voom.app.admin.users.api;

import java.util.List;

import inc.visor.voom.app.admin.users.dto.UserProfileDto;
import retrofit2.Call;
import retrofit2.http.GET;

public interface UserApi {

    @GET("/api/users")
    Call<List<UserProfileDto>> getUsers();
}
