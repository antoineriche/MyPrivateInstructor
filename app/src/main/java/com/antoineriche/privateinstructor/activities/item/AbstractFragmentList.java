package com.antoineriche.privateinstructor.activities.item;


import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
    private List mListItems;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mListItems = new ArrayList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        view.findViewById(R.id.fab_add).setOnClickListener(v -> mListener.addItem(getAddingActivity()));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new GetList(this.mListener).execute();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpListView();
        getActivity().setTitle(title());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_shuffle) {
            Collections.shuffle(mListItems);
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListListener) {
            mListener = (FragmentListListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentListListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public List getItems() {
        return mListItems;
    }

    private void setUpListView() {
        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mListItems);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    private void refreshList(List pList){
        this.mListItems.clear();
        this.mListItems.addAll(pList);
        ((ArrayAdapter) getListAdapter()).notifyDataSetChanged();
    }

    private void startGettingItems(){
        ((TextView) getView().findViewById(R.id.tv_progress_label)).setText("Récupération des nachos");
    }

    private void itemsRetrieved(List pList){
        getView().findViewById(R.id.ll_list_progress).setVisibility(View.GONE);
        refreshList(pList);
    }

    protected abstract Class<? extends Activity> getAddingActivity();
    protected abstract List getItemsFromDB(SQLiteDatabase database);
    protected abstract String title();



    //FIXME make it static
    public class GetList extends AsyncTask<Void, Integer, List>{

        private FragmentListListener mListener;

        GetList(FragmentListListener pListener){
            this.mListener = pListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startGettingItems();
        }

        @Override
        protected List doInBackground(Void... params) {
            return getItemsFromDB(mListener.getDatabase());
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(List list) {
            super.onPostExecute(list);
            itemsRetrieved(list);
        }

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
            args.putLong(AbstractItemActivity.ARG_ITEM_ID, getItems().get(position).getId());
            mListener.seeItemDetails(AbstractItemActivity.CourseActivity.class, args);
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
        protected Class<? extends Activity> getAddingActivity() {
            return AbstractItemActivity.CourseActivity.class;
        }

        @Override
        protected List<Course> getItemsFromDB(SQLiteDatabase database) {
            return CourseTable.getAllCourses(database);
        }

        @Override
        protected String title() {
            return "Mes cours";
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
            args.putLong(AbstractItemActivity.ARG_ITEM_ID, getItems().get(position).getId());
            mListener.seeItemDetails(AbstractItemActivity.PupilActivity.class, args);
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
        protected Class<? extends Activity> getAddingActivity() {
            return AbstractItemActivity.PupilActivity.class;
        }

        @Override
        protected List<Pupil> getItemsFromDB(SQLiteDatabase database) {
            return PupilTable.getAllPupils(database);
        }

        @Override
        protected String title() {
            return "Mes élèves";
        }

        @Override
        public List<Pupil> getItems() {
            return ((List<Pupil>) super.getItems());
        }
    }
}
