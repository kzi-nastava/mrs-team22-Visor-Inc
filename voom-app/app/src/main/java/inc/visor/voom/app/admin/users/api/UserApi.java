package inc.visor.voom.app.admin.users.api;

import java.util.List;
import java.util.Map;

import inc.visor.voom.app.admin.users.dto.UserProfileDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserApi {

    @GET("/api/users")
    Call<List<UserProfileDto>> getUsers();

    @POST("/api/users/{id}/block")
    Call<UserProfileDto> blockUser(
            @Path("id") Long id,
            @Body Map<String, String> body
    );
}
