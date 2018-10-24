package com.antoineriche.privateinstructor.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.antoineriche.privateinstructor.R;

public class NotificationTest {

    private static String CHANNEL_ID = "12";

    public static void create(Context pContext){
        createNotificationChannel(pContext);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(pContext, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("My notif")
                .setContentText("Try to put it here")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(pContext);
        notificationManager.notify(97, mBuilder.build());
    }

    private static void createNotificationChannel(Context pContext) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = pContext.getString(R.string.channel_name);
            CharSequence name = "channel name";
//            String description = pContext.getString(R.string.channel_description);
            String description = "channel desc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = pContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
