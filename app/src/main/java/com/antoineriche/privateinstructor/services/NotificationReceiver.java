package com.antoineriche.privateinstructor.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.beans.Snapshot;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.database.MyDatabase;
import com.antoineriche.privateinstructor.notifications.AbstractNotification;
import com.antoineriche.privateinstructor.notifications.CourseNotification;
import com.antoineriche.privateinstructor.notifications.SnapshotNotification;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = NotificationReceiver.class.getSimpleName();

    public static String ITEM_ID = "item-id";
    public static String ERROR = "error";
    public static String ITEM = "item";
    public static String NOTIFICATION_ARGS = "notification-args";
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION_CODE = "notification-code";
    public static int CANCEL_NOTIFICATION = 18;


    @Override
    public void onReceive(Context context, Intent intent) {

        int requestCode = intent.getExtras().getInt(NOTIFICATION_CODE,-1);
        Log.e(TAG, "RequestCode: " + requestCode);

        if(AbstractNotification.dealsWithCourse(requestCode)) {

            SQLiteDatabase mDatabase = new MyDatabase(context, null).getReadableDatabase();
            long courseId = intent.getExtras().getLong(ITEM_ID, -1);
            Course course = CourseTable.getCourseWithId(mDatabase, courseId);

            if (AbstractNotification.COURSE_BEGINNING == requestCode) {
                new CourseNotification.BeginningCourseNotification(course).create(context);
            } else if (AbstractNotification.COURSE_END == requestCode) {
                new CourseNotification.EndingCourseNotification(course).create(context);
            }
        }

        else if(AbstractNotification.dealsWithSnapshot(requestCode)) {
            if (AbstractNotification.SNAPSHOT_TIME == requestCode) {
                new SnapshotNotification.TimeForSnapshotNotification().create(context);

                intent = new Intent(context, FirebaseIntentService.class);
                intent.putExtra(FirebaseIntentService.FIB_TASKS, new String[]{FirebaseIntentService.FIB_SAVE_NEW_SNAPSHOT});
                context.startService(intent);
            } else if(AbstractNotification.SNAPSHOT_NEW_ONE == requestCode) {
                Snapshot snapshot = (Snapshot) intent.getExtras().getBundle(NOTIFICATION_ARGS).getSerializable(ITEM);
                new SnapshotNotification.NewSnapshotNotification(snapshot).create(context);
            } else if (AbstractNotification.SNAPSHOT_FAILURE == requestCode) {
                String error = intent.getStringExtra(ERROR);
                new SnapshotNotification.FailSavingSnapshotNotification(error).create(context);
            }
        }

        else if (CANCEL_NOTIFICATION == requestCode){
            int notificationId = intent.getExtras().getInt(NOTIFICATION_ID, -1);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.cancel(notificationId);
        }

        else {
            Log.e(TAG, "Request code leads nowhere: " + requestCode);
        }
    }
}
