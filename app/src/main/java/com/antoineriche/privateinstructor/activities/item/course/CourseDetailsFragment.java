package com.antoineriche.privateinstructor.activities.item.course;

import android.app.AlertDialog;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.activities.item.AbstractDetailsItemFragment;
import com.antoineriche.privateinstructor.activities.item.AbstractItemActivity;
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.beans.Pupil;
import com.antoineriche.privateinstructor.database.CourseTable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CourseDetailsFragment extends AbstractDetailsItemFragment {

    public CourseDetailsFragment() {
    }

    public static CourseDetailsFragment newInstance(long pCourseId) {
        CourseDetailsFragment fragment = new CourseDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(AbstractItemActivity.ARG_ITEM_ID, pCourseId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected Course getItemFromDB(SQLiteDatabase pDatabase, long pId) {
        return CourseTable.getCourseWithId(pDatabase, pId);
    }

    @Override
    protected boolean deleteItemFromDB(SQLiteDatabase pDatabase, long pId) {
        return CourseTable.removeCourseWithID(pDatabase, pId);
    }

    @Override
    protected int layout() {
        return R.layout.course_details;
    }

    @Override
    protected void fillViewWithItem(View pView, Object pItem) {
        Course course = (Course) pItem;
        ((TextView) pView.findViewById(R.id.tv_course_pupil)).setText(course.getPupil().toString());
        ((TextView) pView.findViewById(R.id.tv_course_date)).setText(course.getFriendlyDate());

        ((TextView) pView.findViewById(R.id.tv_course_hour)).setText(course.getFriendlyTimeSlot());
        ((TextView) pView.findViewById(R.id.tv_course_money)).setText(String.format(Locale.FRANCE, "%.02f", course.getMoney()));

        ((TextView) pView.findViewById(R.id.tv_course_chapter)).setText(course.getChapter());
        ((TextView) pView.findViewById(R.id.tv_course_comment)).setText(course.getComment());
    }

    @Override
    protected String deletionDialogMessage() {
        return getString(R.string.dialog_delete_course);
    }
}
