package inc.visor.voom.app.shared.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import inc.visor.voom.app.MainActivity;
import inc.visor.voom.app.R;
import inc.visor.voom.app.network.RetrofitClient;
import inc.visor.voom.app.shared.api.NotificationApi;

public class NotificationService {

    private static final String CHANNEL_ID = "voom_channel";

    public static void createChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManager manager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel existing =
                    manager.getNotificationChannel(CHANNEL_ID);

            if (existing != null) return;

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Voom Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription("Ride and system notifications");
            channel.enableVibration(true);

            manager.createNotificationChannel(channel);
        }
    }


    public static void showNotification(
            Context context,
            String title,
            Long notificationId,
            String message
    ) {
        createChannel(context);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

        Notification notification =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .build();


        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int id = notificationId != null
                ? notificationId.intValue()
                : (int) System.currentTimeMillis();

        manager.notify(id, notification);

        markNotificationAsRead(context, notificationId);
    }

    public static void showRideTrackingNotification(
            Context context,
            String title,
            Long notificationId,
            String message
    ) {
        createChannel(context);

        Uri deepLinkUri = Uri.parse("voom://ride/tracking");

        Intent intent = new Intent(Intent.ACTION_VIEW, deepLinkUri);
        intent.setPackage(context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        context,
                        notificationId != null ? notificationId.intValue() : 1,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

        Notification notification =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .build();

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int id = notificationId != null
                ? notificationId.intValue()
                : (int) System.currentTimeMillis();

        manager.notify(id, notification);

        markNotificationAsRead(context, notificationId);
    }

    private static void markNotificationAsRead(Context context, Long id) {

        NotificationApi api =
                RetrofitClient.getInstance()
                        .create(NotificationApi.class);

        api.markAsRead(id)
                .enqueue(new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(
                            retrofit2.Call<Void> call,
                            retrofit2.Response<Void> response
                    ) {
                        android.util.Log.d("NOTIF", "Marked as read");
                    }

                    @Override
                    public void onFailure(
                            retrofit2.Call<Void> call,
                            Throwable t
                    ) {
                        android.util.Log.e("NOTIF", "Failed to mark read", t);
                    }
                });
    }


}
