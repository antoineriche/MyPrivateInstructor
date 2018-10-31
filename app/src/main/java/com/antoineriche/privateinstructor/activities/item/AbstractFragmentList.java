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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.beans.DatabaseItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractFragmentList extends Fragment {

    private FragmentListListener mListener;
    private List<DatabaseItem> mListItems;
    private RecyclerView.Adapter mRVAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mListItems = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.fab_add).setOnClickListener(v -> mListener.goToAddingActivity(getAddingActivity()));
    }

    @Override
    public void onStart() {
        super.onStart();
        new GetList(this.mListener).execute();
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpRecyclerView(getRecyclerView());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_menu, menu);
        Drawable drawable = menu.findItem(R.id.action_shuffle).getIcon();

        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(getActivity(), R.color.white));
    }


    private void setUpRecyclerView(RecyclerView recyclerView){
        mRVAdapter = initAdapter(getListItems(), mListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mRVAdapter);
    }

    public List<DatabaseItem> getListItems() {
        return mListItems;
    }

    protected RecyclerView getRecyclerView(){
        return getView().findViewById(R.id.rv_list_item);
    }

    protected RecyclerView.Adapter getAdapter(){
        return mRVAdapter;
    }

    private void startGettingItems(){
        mListItems.clear();
        ((TextView) getView().findViewById(R.id.tv_progress_label)).setText("Récupération des nachos");
    }

    private void onItemsFromDatabase(List<DatabaseItem> pItemsFromDatabase){
        getView().findViewById(R.id.ll_list_progress).setVisibility(View.GONE);
        mListItems.addAll(pItemsFromDatabase);
        refreshRecyclerViewData(mListItems);
    }

    protected void refreshRecyclerViewData(List<DatabaseItem> pNewData){
        getAdapter().notifyDataSetChanged();
    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_shuffle) {
            Collections.shuffle(mListItems);
            refreshRecyclerViewData(mListItems);
        } else if (item.getItemId() == R.id.action_order) {
            refreshRecyclerViewData(order(mListItems));
        }
        return true;
    }

    protected abstract Class<? extends Activity> getAddingActivity();
    protected abstract List<DatabaseItem> order(List<DatabaseItem> pItemsToOrder);
    protected abstract List<DatabaseItem> getItemsFromDB(SQLiteDatabase database);
    protected abstract RecyclerView.Adapter initAdapter(List<DatabaseItem> pListItems, FragmentListListener pListener);


    //FIXME make it static
    public class GetList extends AsyncTask<Void, Integer, List<DatabaseItem>>{

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
        protected List<DatabaseItem> doInBackground(Void... params) {
            return getItemsFromDB(mListener.getDatabase());
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<DatabaseItem> list) {
            super.onPostExecute(list);
            onItemsFromDatabase(list);
        }
    }



    public interface FragmentListListener {
        SQLiteDatabase getDatabase();

        void goToAddingActivity(Class pActivity);

        void goToDetailsActivity(Class pActivity, Bundle pBundle);
    }

}
