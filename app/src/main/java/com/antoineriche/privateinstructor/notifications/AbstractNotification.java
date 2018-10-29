package com.antoineriche.privateinstructor.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.services.NotificationReceiver;

public abstract class AbstractNotification {

    abstract String getTitle();
    abstract String getContent();
    abstract String getChannelId();
    abstract String getChannelName();
    abstract String getChannelDescription();
    abstract int getChannelImportance();
    abstract int getNotificationId();
    abstract PendingIntent getPendingIntent(Context pContext);
    abstract boolean isClickable();
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

    public NotificationCompat.Builder build(Context pContext){
        return new NotificationCompat.Builder(pContext, getChannelName())
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getTitle())
                .setContentText(getContent())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(getContent()))
                .setContentIntent(isClickable() ? getPendingIntent(pContext) : null)
                .setAutoCancel(autoCancelable())
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setLights(0x81d4fa, 3000, 1500);
    }

    public void create(Context pContext) {
        createNotificationChannel(pContext);
        NotificationCompat.Builder builder = build(pContext);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(pContext);
        notificationManager.notify(getNotificationId(), builder.build());
    }

    PendingIntent getCancelNotificationIntent(Context pContext){
        Intent intentAlarm = new Intent(pContext, NotificationReceiver.class);
        intentAlarm.putExtra(NotificationReceiver.NOTIFICATION_CODE, NotificationReceiver.CANCEL_NOTIFICATION);
        intentAlarm.putExtra(NotificationReceiver.NOTIFICATION_ID, getNotificationId());
        return PendingIntent.getBroadcast(pContext, NotificationReceiver.CANCEL_NOTIFICATION,
                intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
