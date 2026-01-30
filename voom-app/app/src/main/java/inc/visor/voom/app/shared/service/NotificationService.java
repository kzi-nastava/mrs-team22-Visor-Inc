package inc.visor.voom.app.shared.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import inc.visor.voom.app.R;
import android.content.Context;

public class NotificationService {

    private static final String CHANNEL_ID = "ride_channel";

    public static void showArrivalNotification(Context context) {

        NotificationManager manager =
                (NotificationManager)
                        context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Ride Updates",
                            NotificationManager.IMPORTANCE_HIGH
                    );
            manager.createNotificationChannel(channel);
        }

        Notification notification =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle("Driver arrived 🚗")
                        .setContentText("Your driver has arrived at pickup location.")
                        .setSmallIcon(R.drawable.ic_location_24)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .build();

        manager.notify(1001, notification);
    }
    public static void showRideAssignedNotification(
            Context context,
            String pickupAddress
    ) {

        NotificationManager manager =
                (NotificationManager)
                        context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Ride Updates",
                            NotificationManager.IMPORTANCE_HIGH
                    );
            manager.createNotificationChannel(channel);
        }

        Notification notification =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle("New Ride Assigned 🚖")
                        .setContentText("Pickup: " + pickupAddress)
                        .setSmallIcon(R.drawable.ic_driver)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .build();

        manager.notify(1002, notification);
    }



}
