package com.antoineriche.privateinstructor.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.antoineriche.privateinstructor.R;

public abstract class AbstractNotification {

    abstract String getTitle();
    abstract String getContent();
    abstract String getChannelId();
    abstract String getChannelName();
    abstract String getChannelDescription();
    abstract int getChannelImportance();
    abstract int getNotificationId();
    abstract PendingIntent getPendingIntent(Context pContext);
    abstract boolean autoCancelable();


    private void createNotificationChannel(Context pContext) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(getChannelId(), getChannelName(), getChannelImportance());
            channel.setDescription(getChannelDescription());
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = pContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void create(Context pContext) {
        createNotificationChannel(pContext);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(pContext, getChannelName())
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getTitle())
                .setContentText(getContent())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(getContent()))
                .setContentIntent(getPendingIntent(pContext))
                .setAutoCancel(autoCancelable())
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setLights(0x81d4fa, 3000, 1500);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(pContext);
        notificationManager.notify(getNotificationId(), builder.build());
    }

}
