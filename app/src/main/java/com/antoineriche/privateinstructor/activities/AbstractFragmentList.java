package com.antoineriche.privateinstructor.activities;


import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.beans.Pupil;
import com.antoineriche.privateinstructor.database.CourseTable;
import com.antoineriche.privateinstructor.database.PupilTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractFragmentList extends ListFragment implements AdapterView.OnItemClickListener {

    protected FragmentListListener mListener;
    private List mListItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mListItem = new ArrayList();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        retrieveItems(mListener.getDatabase());
        setUpListView();
    }

    protected abstract void retrieveItems(SQLiteDatabase database);

    private void setUpListView(){
        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mListItem);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_shuffle){
            Collections.shuffle(mListItem);
            ((ArrayAdapter) getListAdapter()).notifyDataSetChanged();
        }
        return true;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_menu, menu);
        Drawable drawable = menu.findItem(R.id.action_shuffle).getIcon();

        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(getActivity(), R.color.white));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        view.findViewById(R.id.fab_add).setOnClickListener(v -> mListener.addItem(getAddingActivity()));
        return view;
    }

    protected abstract Class <? extends Activity> getAddingActivity();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListListener) {
            mListener = (FragmentListListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentListListener");
        }
    }

    public List getItems(){
        return mListItem;
    };

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }




    public interface FragmentListListener {
        SQLiteDatabase getDatabase();
        void addItem(Class pActivity);
        void seeItemDetails(Class pActivity, Bundle pBundle);
    }



    public static class CourseListFragment extends AbstractFragmentList {

        public CourseListFragment() {
        }

        public static CourseListFragment newInstance() {
            return new CourseListFragment();
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            Bundle args = new Bundle();
            args.putInt(CourseActivity.ARG_COURSE_ID, getItems().get(position).getId());
            mListener.seeItemDetails(CourseActivity.class, args);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            super.onOptionsItemSelected(item);

            if (item.getItemId() == R.id.action_order) {
                getItems().sort(Comparator.comparing(Course::getId));
                ((ArrayAdapter) getListAdapter()).notifyDataSetChanged();
            }

            return true;
        }

        @Override
        protected Class <? extends Activity> getAddingActivity() {
            return CourseActivity.class;
        }

        protected void retrieveItems(SQLiteDatabase database){
            getItems().addAll(CourseTable.getAllCourses(database));
        }

        @Override
        public List<Course> getItems() {
            return ((List<Course>) super.getItems());
        }
    }


    public static class PupilListFragment extends AbstractFragmentList {

        public PupilListFragment() {
        }

        public static PupilListFragment newInstance() {
            return new PupilListFragment();
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            Bundle args = new Bundle();
            args.putInt(PupilActivity.ARG_PUPIL_ID, getItems().get(position).getId());
            mListener.seeItemDetails(PupilActivity.class, args);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            super.onOptionsItemSelected(item);

            if (item.getItemId() == R.id.action_order) {
                getItems().sort(Comparator.comparing(Pupil::getId));
                ((ArrayAdapter) getListAdapter()).notifyDataSetChanged();
            }

            return true;
        }

        @Override
        protected Class <? extends Activity> getAddingActivity() {
            return PupilActivity.class;
        }

        protected void retrieveItems(SQLiteDatabase database){
            getItems().addAll(PupilTable.getAllPupils(database));
        }

        @Override
        public List<Pupil> getItems() {
            return ((List<Pupil>) super.getItems());
        }
    }
}
