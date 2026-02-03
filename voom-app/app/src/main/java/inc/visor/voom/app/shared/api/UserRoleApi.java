package inc.visor.voom.app.shared.api;

import java.util.List;

import inc.visor.voom.app.shared.dto.user.UserRoleDto;
import retrofit2.Call;
import retrofit2.http.GET;

public interface UserRoleApi {

    @GET("/api/roles")
    Call<List<UserRoleDto>> getUserRoles();

}
