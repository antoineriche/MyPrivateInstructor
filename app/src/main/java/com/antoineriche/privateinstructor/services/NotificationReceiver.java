package com.antoineriche.privateinstructor.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.database.MyDatabase;
import com.antoineriche.privateinstructor.notifications.CourseNotification;

public class NotificationReceiver extends BroadcastReceiver {

    public static String ITEM_ID = "item-id";
    public static String NOTIFICATION_CODE = "notification-code";



    @Override
    public void onReceive(Context context, Intent intent) {

        int requestCode = intent.getExtras().getInt(NOTIFICATION_CODE,-1);

        if(CourseNotification.NOTIFICATION_CODES.contains(requestCode)) {

            SQLiteDatabase mDatabase = new MyDatabase(context, null).getReadableDatabase();
            long courseId = intent.getExtras().getLong(ITEM_ID, -1);
            Course course = CourseTable.getCourseWithId(mDatabase, courseId);

            if (CourseNotification.BEGINNING_COURSE_CODE == requestCode) {
                new CourseNotification.BeginningCourseNotification(course).create(context);
            } else if (CourseNotification.END_COURSE_CODE == requestCode) {
                new CourseNotification.EndingCourseNotification(course).create(context);
            }
        }
    }
}
