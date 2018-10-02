package com.antoineriche.privateinstructor.activities;


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
import com.antoineriche.privateinstructor.database.CourseTable;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public abstract class AbstractFragmentList extends ListFragment implements AdapterView.OnItemClickListener {

    protected FragmentListListener mListener;
    protected FloatingActionButton mFABAdd;

    protected abstract void setUpListAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpListAdapter();
        getListView().setOnItemClickListener(this);
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
        mFABAdd = view.findViewById(R.id.fab_add);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListListener) {
            mListener = (FragmentListListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentListListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface FragmentListListener {
        SQLiteDatabase getDatabase();
    }



    public static class CourseListFragment extends AbstractFragmentList {

        private List<Course> mListCourses;

        public CourseListFragment() {
        }

        public static CourseListFragment newInstance() {
            return new CourseListFragment();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_shuffle:
                    Collections.shuffle(mListCourses);
                    ((ArrayAdapter) getListAdapter()).notifyDataSetChanged();
                    break;
                case R.id.action_order:
                    mListCourses.sort(Comparator.comparing(Course::getId));
                    ((ArrayAdapter) getListAdapter()).notifyDataSetChanged();
                    break;
            }
            return true;

        }

        @Override
        protected void setUpListAdapter() {
            mListCourses = CourseTable.getAllCourses(mListener.getDatabase());
            ArrayAdapter<Course> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mListCourses);
            setListAdapter(adapter);
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            Toast.makeText(getActivity(), mListCourses.get(position).toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            mFABAdd.setOnClickListener( v -> {
                Course c = new Course( System.currentTimeMillis(), Course.DURATION_1H, Course.FORESEEN, 25, "Conversions", "RAS", 2);
                CourseTable.insertCourse( mListener.getDatabase(), c);
                mListCourses.add(c);
                ((ArrayAdapter) getListAdapter()).notifyDataSetChanged();
            });
        }
    }
}
