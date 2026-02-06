package inc.visor.voom.app.shared.api;

import java.util.List;

import inc.visor.voom.app.shared.dto.NotificationDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NotificationApi {

    @POST("/api/notifications/{id}/read")
    Call<Void> markAsRead(@Path("id") Long id);

    @GET("/api/notifications/unread")
    Call<List<NotificationDto>> getUnread();
}
