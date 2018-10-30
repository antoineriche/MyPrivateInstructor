package com.antoineriche.privateinstructor.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.database.MyDatabase;
import com.antoineriche.privateinstructor.notifications.CourseNotification;
import com.antoineriche.privateinstructor.utils.DateUtils;
import com.antoineriche.privateinstructor.utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CourseCheckingService extends Service {

    private static String LOG_TAG = "CourseCheckingService";
    private SQLiteDatabase mDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "in onCreate");
        mDatabase = new MyDatabase(this, null).getWritableDatabase();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // Update past courses from foreseen to waiting state
        getPastCoursesInForeseenState().forEach(this::putCourseInWaitingState);

        // Schedule next course beginning notification
        Course course = getNextCourse();
        if(course != null){ scheduleAlarmForNextCourse(course); }

        stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mDatabase != null) {
            mDatabase.close();
            Log.v(LOG_TAG, "database closed");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Get past courses with state FORESEEN {@link Course}
     * @return courses as list
     */
    private List<Course> getPastCoursesInForeseenState(){
        List<Course> comingCourses = this.mDatabase != null ?
                CourseTable.getCoursesWithState(this.mDatabase, Course.FORESEEN)
                : new ArrayList<>();

        return comingCourses.stream().filter(c -> DateUtils.isPast(c.getEndDate())).collect(Collectors.toList());
    }

    private Course getNextCourse(){
        return CourseTable.getNextCourse(mDatabase);
    }

    private void putCourseInWaitingState(Course course){
        if(this.mDatabase != null){
            course.setState(Course.WAITING_FOR_VALIDATION);
            CourseTable.updateCourse(mDatabase, course.getId(), course);
        } else {
            Log.e(LOG_TAG, "could not update course, database was null");
        }
    }

    //FIXME: notification before course
    private void scheduleAlarmForNextCourse(Course pCourse){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intentAlarm;
        intentAlarm = new Intent(getApplicationContext(), NotificationReceiver.class);
        intentAlarm.putExtra(NotificationReceiver.ITEM_ID, pCourse.getId());


        // Schedule start notification
        if(PreferencesUtils.getBooleanPreferences(this, getString(R.string.pref_course_beginning))) {
            intentAlarm.putExtra(NotificationReceiver.NOTIFICATION_CODE, CourseNotification.BEGINNING_COURSE_CODE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, pCourse.getDate(),
                    PendingIntent.getBroadcast(getApplicationContext(), CourseNotification.BEGINNING_COURSE_CODE,
                            intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT)
            );
        }

        // Schedule end notification
        if(PreferencesUtils.getBooleanPreferences(this, getString(R.string.pref_course_end))) {
            intentAlarm.putExtra(NotificationReceiver.NOTIFICATION_CODE, CourseNotification.END_COURSE_CODE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, pCourse.getEndDate(),
                    PendingIntent.getBroadcast(getApplicationContext(), CourseNotification.END_COURSE_CODE,
                            intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT)
            );
        }

//        // Hack
//        if(PreferencesUtils.getBooleanPreferences(this, getString(R.string.pref_course_beginning))) {
//            intentAlarm.putExtra(NotificationReceiver.NOTIFICATION_CODE, CourseNotification.BEGINNING_COURSE_CODE);
//            alarmManager.set(AlarmManager.RTC_WAKEUP, new Date().getTime() + 3 * 1000,
//                    PendingIntent.getBroadcast(getApplicationContext(), CourseNotification.BEGINNING_COURSE_CODE,
//                            intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT)
//            );
//        }
    }
}
