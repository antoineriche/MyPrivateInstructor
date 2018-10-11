package com.antoineriche.privateinstructor.activities.item.course;

import android.database.sqlite.SQLiteDatabase;
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
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.FRANCE);

        String stDate = sdf.format(course.getDate());
        stDate = stDate.substring(0, 1).toUpperCase().concat(stDate.substring(1));
        ((TextView) pView.findViewById(R.id.tv_course_date)).setText(stDate);

        sdf = new SimpleDateFormat("HH'h'mm", Locale.FRANCE);
        String stHour = sdf.format(course.getDate());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(course.getDate());
        calendar.add(Calendar.MINUTE, course.getDuration());
        stHour = String.format("%s - %s", stHour, sdf.format(calendar.getTime()));
        ((TextView) pView.findViewById(R.id.tv_course_hour)).setText(stHour);

        ((TextView) pView.findViewById(R.id.tv_course_money)).setText(String.format(Locale.FRANCE, "%.02f", course.getMoney()));

        ((TextView) pView.findViewById(R.id.tv_course_chapter)).setText(course.getChapter());
        ((TextView) pView.findViewById(R.id.tv_course_comment)).setText(course.getComment());
    }

    @Override
    protected String title(Object pItem) {
        return String.format(Locale.FRANCE, "Cours %03d", ((Course) pItem).getId());
    }
}
