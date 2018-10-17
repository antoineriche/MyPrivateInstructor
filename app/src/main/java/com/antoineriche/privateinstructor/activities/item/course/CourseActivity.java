package com.antoineriche.privateinstructor.activities.item.course;

import android.support.v4.app.Fragment;

import com.antoineriche.privateinstructor.activities.item.AbstractItemActivity;
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.database.CourseTable;

public class CourseActivity extends AbstractItemActivity {

    @Override
    protected Fragment getEditionFragment(long pItemId) {
        return CourseFormFragment.newInstance(pItemId);
    }

    @Override
    protected Fragment getDetailsFragment(long pItemId) {
        return CourseDetailsFragment.newInstance(pItemId);
    }

    @Override
    protected Fragment getAddingFragment() {
        return CourseFormFragment.newInstance();
    }

    @Override
    protected long insertItemInDatabase(Object pItem) {
        return CourseTable.insertCourse(getDatabase(), (Course) pItem);
    }

    @Override
    protected void updateItemInDatabase(long mItemId, Object pItem) {
        CourseTable.updateCourse(getDatabase(), mItemId, (Course) pItem);
    }

    @Override
    protected boolean removeItemFromDatabase(long mItemId) {
        return CourseTable.removeCourseWithID(getDatabase(), mItemId);
    }

    @Override
    public Course getItemFromDb(long pId) {
        return CourseTable.getCourseWithId(getDatabase(), pId);
    }
}
