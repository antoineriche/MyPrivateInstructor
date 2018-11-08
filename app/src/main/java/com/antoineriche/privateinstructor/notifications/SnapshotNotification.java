package com.antoineriche.privateinstructor.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.activities.item.AbstractItemActivity;
import com.antoineriche.privateinstructor.activities.item.course.CourseActivity;
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.beans.Snapshot;
import com.antoineriche.privateinstructor.services.NotificationReceiver;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public abstract class SnapshotNotification extends AbstractNotification {

    @Override
    String getChannelId() {
        return "SNAPSHOTS_NOTIFICATION";
    }

    @Override
    String getChannelName() {
        return "Snapshots";
    }

    @Override
    String getChannelDescription() {
        return "Gestion des snapshots.";
    }

    @Override
    int getChannelImportance() {
        return NotificationManager.IMPORTANCE_DEFAULT;
    }

    @Override
    boolean autoCancelable() {
        return true;
    }

    @Override
    PendingIntent getPendingIntent(Context pContext) { return null; }

    @Override
    boolean isClickable() {
        return false;
    }

    @Override
    String getTitle() {
        return "Snapshot";
    }

    public static class TimeForSnapshotNotification extends SnapshotNotification {

        public TimeForSnapshotNotification(){}

        @Override
        String getContent() {
            return "La sauvegarde a commencé.";
        }

        @Override
        int getNotificationId() {
            return SNAPSHOT_TIME;
        }
    }

    public static class NewSnapshotNotification extends SnapshotNotification {

        private final Snapshot mRSnapshot;

        public NewSnapshotNotification(Snapshot snapshot){
            this.mRSnapshot = snapshot;
        }

        @Override
        String getContent() {
            return String.format(Locale.FRANCE, "Une nouvelle snapshot a été sauvegardée.\n(%s: %d cours, %d élèves, %d devoirs, %d adresses)",
                    mRSnapshot.getName(), mRSnapshot.getCourses().size(), mRSnapshot.getPupils().size(),
                    mRSnapshot.getDevoirs().size(), mRSnapshot.getLocations().size());
        }

        @Override
        int getNotificationId() {
            return SNAPSHOT_NEW_ONE;
        }
    }

    public static class FailSavingSnapshotNotification extends SnapshotNotification {

        private final String mError;

        public FailSavingSnapshotNotification(String error){
            this.mError = error;
        }

        @Override
        String getContent() {
            return String.format(Locale.FRANCE, "Erreur lors de l'enregistrement de la snapshot.\n%s", mError);
        }

        @Override
        int getNotificationId() {
            return SNAPSHOT_FAILURE;
        }
    }
}
