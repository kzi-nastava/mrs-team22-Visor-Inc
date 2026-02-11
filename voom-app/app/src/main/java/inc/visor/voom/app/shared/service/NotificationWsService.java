package inc.visor.voom.app.shared.service;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import inc.visor.voom.app.config.AppConfig;
import inc.visor.voom.app.shared.DataStoreManager;
import inc.visor.voom.app.shared.dto.NotificationSocketDto;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompMessage;

public class NotificationWsService {

    private final Context context;
    private final Long userId;
    private StompClient stompClient;

    private final String userRole;
    public NotificationWsService(Context context, Long userId) {
        this.context = context;
        this.userId = userId;
        this.userRole = DataStoreManager.getInstance().getUserRole().blockingGet();
    }

    public void connect() {

        String wsUrl = AppConfig.getWsUrl();

        stompClient = Stomp.over(
                Stomp.ConnectionProvider.OKHTTP,
                wsUrl
        );

        stompClient.connect();

        stompClient.topic("/topic/notifications/" + userId)
                .subscribe(this::handleMessage,
                        throwable -> Log.e("WS", "Error", throwable));
    }

    private void handleMessage(StompMessage stompMessage) {

        String json = stompMessage.getPayload();

        Log.d("NOTIFICATION_WS", "Received: " + json);

        NotificationSocketDto dto =
                new Gson().fromJson(json, NotificationSocketDto.class);

        String notificationType = dto.type;

        if (userRole.equals("USER") && notificationType.equals("RIDE_STARTED")) {
            NotificationService.showRideTrackingNotification(context, dto.title, dto.id, dto.message);
        }
        else {
            NotificationService.showNotification(
                    context,
                    dto.title,
                    dto.id,
                    dto.message
            );
        }

    }

    public void disconnect() {

        if (stompClient != null) {
            stompClient.disconnect();
            stompClient = null;
            Log.d("WS", "Disconnected notification WS");
        }
    }

}
