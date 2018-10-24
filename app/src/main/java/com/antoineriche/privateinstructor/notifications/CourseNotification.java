package com.antoineriche.privateinstructor.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.antoineriche.privateinstructor.activities.item.AbstractItemActivity;
import com.antoineriche.privateinstructor.activities.item.course.CourseActivity;
import com.antoineriche.privateinstructor.beans.Course;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public abstract class CourseNotification extends AbstractNotification {

    public static int BEGINNING_COURSE_CODE = 91;
    public static int END_COURSE_CODE = 92;
    public static List<Integer> NOTIFICATION_CODES = Arrays.asList(BEGINNING_COURSE_CODE, END_COURSE_CODE);

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
            return BEGINNING_COURSE_CODE;
        }

        @Override
        PendingIntent getPendingIntent(Context pContext) {
            Intent launchIntent = new Intent(pContext, CourseActivity.class);
            launchIntent.putExtra(AbstractItemActivity.ARG_ITEM_ID, course.getId());

            return PendingIntent.getActivity(pContext, BEGINNING_COURSE_CODE,
                    launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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
            return END_COURSE_CODE;
        }

        @Override
        PendingIntent getPendingIntent(Context pContext) {
            Intent launchIntent = new Intent(pContext, CourseActivity.class);
            launchIntent.putExtra(AbstractItemActivity.ARG_ITEM_ID, course.getId());

            return PendingIntent.getActivity(pContext, END_COURSE_CODE,
                    launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }
}
