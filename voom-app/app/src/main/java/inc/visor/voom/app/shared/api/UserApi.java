package inc.visor.voom.app.shared.api;

import java.util.List;

import inc.visor.voom.app.shared.dto.user.CreateUserDto;
import inc.visor.voom.app.shared.dto.user.UserProfileDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserApi {

    @GET("/api/users")
    Call<List<UserProfileDto>> getUsers();

    @GET("/api/users/{userId}")
    Call<UserProfileDto> getUser(@Path("userId") Long userId);

    @POST("/api/users")
    Call<UserProfileDto> createUser(@Body CreateUserDto dto);

    @PUT("/api/users/{userId}")
    Call<UserProfileDto> updateUser(
            @Path("userId") Long userId,
            @Body UserProfileDto dto
    );

    @DELETE("/api/users/{userId}")
    Call<Void> deleteUser(@Path("userId") Long userId);

}
