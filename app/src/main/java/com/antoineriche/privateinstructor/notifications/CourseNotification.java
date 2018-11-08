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
import com.antoineriche.privateinstructor.services.NotificationReceiver;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public abstract class CourseNotification extends AbstractNotification {

    @Override
    String getChannelId() {
        return "COURSES_NOTIFICATION";
    }

    @Override
    String getChannelName() {
        return "Cours";
    }

    @Override
    String getChannelDescription() {
        return "Début et fin de cours.";
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


    public static class BeginningCourseNotification extends CourseNotification {

        private Course course;

        public BeginningCourseNotification(Course pCourse) {
            this.course = pCourse;
        }

        @Override
        String getTitle() {
            return "Début de cours";
        }

        @Override
        String getContent() {
            return String.format(Locale.FRANCE, "Le cours avec %s a commencé.", course.getPupil().getFullName());
        }

        @Override
        int getNotificationId() {
            return COURSE_BEGINNING;
        }

    }

    public static class EndingCourseNotification extends CourseNotification {

        private Course course;

        public EndingCourseNotification(Course pCourse) {
            this.course = pCourse;
        }

        @Override
        String getTitle() {
            return "Fin de cours";
        }

        @Override
        String getContent() {
            return String.format(Locale.FRANCE, "Le cours avec %s est terminé.", course.getPupil().getFullName());
        }

        @Override
        int getNotificationId() {
            return COURSE_END;
        }

        @Override
        public NotificationCompat.Builder build(Context pContext) {
            NotificationCompat.Builder builder = super.build(pContext);
            builder.addAction(R.drawable.baseline_create_black_48, "Compléter", getCompleteCoursePendingIntent(pContext));
            builder.addAction(R.drawable.baseline_close_white_48, "Plus tard", getCancelNotificationIntent(pContext));
            return builder;
        }

        private PendingIntent getCompleteCoursePendingIntent(Context pContext){
            Intent launchIntent = new Intent(pContext, CourseActivity.class);
            launchIntent.putExtra(AbstractItemActivity.ARG_ITEM_ID, course.getId());
            launchIntent.putExtra(AbstractItemActivity.ARG_ITEM_EDITION, true);
            launchIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, getNotificationId());
            return PendingIntent.getActivity(pContext, COURSE_END,
                    launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

    }
}
