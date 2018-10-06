package com.antoineriche.privateinstructor.activities.item.course;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.activities.item.AbstractDetailsItemFragment;
import com.antoineriche.privateinstructor.activities.item.AbstractItemActivity;
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.database.CourseTable;

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
        return R.layout.course_fragment_details;
    }

    @Override
    protected void fillViewWithItem(View pView, Object pItem) {
        Course course = (Course) pItem;
        ((TextView) pView.findViewById(R.id.tv_course_detail)).setText(course.toString());
    }
}
