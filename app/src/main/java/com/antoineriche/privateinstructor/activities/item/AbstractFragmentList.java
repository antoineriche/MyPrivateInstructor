package com.antoineriche.privateinstructor.activities.item;


import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.database.CourseTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractFragmentList extends Fragment {

    private FragmentListListener mListener;
    private List mListItems;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mListItems = new ArrayList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.fab_add).setOnClickListener(v -> mListener.addItem(getAddingActivity()));
    }

    @Override
    public void onResume() {
        super.onResume();
        new GetList(this.mListener).execute();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(title());
        getRecyclerView().setLayoutManager(new LinearLayoutManager(getContext()));
        setUpRecyclerView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_shuffle) {
            Collections.shuffle(mListItems);
            getRecyclerView().getAdapter().notifyDataSetChanged();
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

    private void setUpRecyclerView(){
        getRecyclerView().setAdapter(getAdapter(mListItems, mListener));
    }

    private void refreshList(List pList){
        this.mListItems.clear();
        this.mListItems.addAll(pList);
        getRecyclerView().getAdapter().notifyDataSetChanged();
    }

    private void startGettingItems(){
        ((TextView) getView().findViewById(R.id.tv_progress_label)).setText("Récupération des nachos");
    }

    private void itemsRetrieved(List pList){
        getView().findViewById(R.id.ll_list_progress).setVisibility(View.GONE);
        refreshList(pList);
    }

    private RecyclerView getRecyclerView(){
        return getView().findViewById(R.id.rv_list_item);
    }

    protected void refreshListItems(){
        getRecyclerView().getAdapter().notifyDataSetChanged();
    }

    protected abstract Class<? extends Activity> getAddingActivity();
    protected abstract List getItemsFromDB(SQLiteDatabase database);
    protected abstract String title();
    protected abstract RecyclerView.Adapter getAdapter(List pListItems, FragmentListListener pListener);


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

}
