package com.antoineriche.privateinstructor.activities.item;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.activities.ToImplementFragment;
import com.antoineriche.privateinstructor.activities.item.course.CourseDetailsFragment;
import com.antoineriche.privateinstructor.activities.item.course.CourseFormFragment;
import com.antoineriche.privateinstructor.activities.item.pupil.PupilDetailsFragment;
import com.antoineriche.privateinstructor.activities.item.pupil.PupilFormFragment;
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.beans.Pupil;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.database.PupilTable;

//FIXME pb de drawer

public abstract class AbstractItemActivity extends AbstractDatabaseActivity
        implements AbstractFormItemFragment.FormListener,
        AbstractDetailsItemFragment.DetailsListener {

    public static final String ARG_ITEM_ID = "item-id";
    public static final String ARG_ITEM_EDITION = "item-edition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        Fragment fragment = getFragmentFromBundle(getIntent().getExtras());
        loadFragment(fragment);

        //API MAP AIzaSyBV1nVo1Mf6NWtf71O-tZW1YPDmakHSTKg

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected boolean isDatabaseWritable() {
        return true;
    }

    protected abstract Fragment getEditionFragment(long pItemId);
    protected abstract Fragment getDetailsFragment(long pItemId);
    protected abstract Fragment getAddingFragment();
    protected abstract long insertItemInDatabase(SQLiteDatabase pDatabase, Object pItem);
    protected abstract void updateItemInDatabase(SQLiteDatabase pDatabase, long mItemId, Object pItem);

    private Fragment getFragmentFromBundle(Bundle pBundle) {
        Fragment fragment;

        // Adding Item
        if (pBundle == null) {
            fragment = getAddingFragment();
        } else {
            long itemId = pBundle.getLong(ARG_ITEM_ID, -1);

            if (itemId != -1) {
                // Editing Item
                if (pBundle.getBoolean(ARG_ITEM_EDITION)) {
                    fragment = getEditionFragment(itemId);
                }
                // Details Item
                else {
                    fragment = getDetailsFragment(itemId);
                }
            } else {
                fragment = ToImplementFragment.newInstance("Item");
            }
        }
        return fragment;
    }

    protected void loadFragment(Fragment pFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, pFragment).commit();
    }

    @Override
    public void itemDeleted() {
        finish();
    }

    @Override
    public void editItem(long mItemId) {
        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, mItemId);
        args.putBoolean(ARG_ITEM_EDITION, true);
        Fragment fragment = getFragmentFromBundle(args);
        loadFragment(fragment);
    }

    @Override
    public void backToDetails(long mItemId) {
        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, mItemId);
        Fragment fragment = getFragmentFromBundle(args);
        loadFragment(fragment);
    }

    @Override
    public void cancel() {
        finish();
    }

    @Override
    public void updateItem(long mItemId, Object pNewItem) {
        updateItemInDatabase(getDatabase(), mItemId, pNewItem);
        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, mItemId);
        Fragment fragment = getFragmentFromBundle(args);
        loadFragment(fragment);
    }

    @Override
    public void saveItem(Object pNewItem) {
        long pId = insertItemInDatabase(getDatabase(), pNewItem);
        Log.e(getClass().getSimpleName(), "Item added");

        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, pId);
        Fragment fragment = getFragmentFromBundle(args);
        loadFragment(fragment);
    }

    public static class CourseActivity extends AbstractItemActivity {

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
        protected long insertItemInDatabase(SQLiteDatabase pDatabase, Object pItem) {
            return CourseTable.insertCourse(getDatabase(), (Course) pItem);
        }

        @Override
        protected void updateItemInDatabase(SQLiteDatabase pDatabase, long mItemId, Object pItem) {
            CourseTable.updateCourse(getDatabase(), mItemId, (Course) pItem);
        }
    }

}
