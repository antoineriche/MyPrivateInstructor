package com.antoineriche.privateinstructor.activities.item;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.antoineriche.privateinstructor.DatabaseItemListener;
import com.antoineriche.privateinstructor.R;

public abstract class AbstractFormItemFragment extends AbstractDatabaseFragment {

    //FIXME
    protected long mItemId = -1;
    private boolean isEditing = false;
    private FormListener mFormListener;
    private DatabaseItemListener mDbItemListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mItemId = getArguments().getLong(AbstractItemActivity.ARG_ITEM_ID, -1);
            isEditing = getArguments().getBoolean(AbstractItemActivity.ARG_ITEM_EDITION, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(layout(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);

        if(isEditing){
            Object item = getItemFromDB(mListener.getDatabase(), mItemId);
            fillViewWithItem(view, item);
        } else {
            cleanView(getView());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FormListener) {
            mFormListener = (FormListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FormListener");
        }

        if (context instanceof DatabaseItemListener) {
            mDbItemListener = (DatabaseItemListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement DatabaseItemListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFormListener = null;
        mDbItemListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.form_item, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.action_cancel) {
            if(isEditing){
                mFormListener.backToDetails(mItemId);
            } else {
                mFormListener.cancel();
            }
        } else if (item.getItemId() == R.id.action_save) {
            try{
                Object newItem = extractItemFromView(getView());
                if (isEditing) {
                    mDbItemListener.updateItem(mItemId, newItem);
                } else {
                    mDbItemListener.saveItem(newItem);
                }
            } catch(Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        return true;
    }

    protected abstract Object extractItemFromView(View view) throws IllegalArgumentException;
    protected abstract void fillViewWithItem(View view, Object item);
    protected abstract void initView(View view);
    protected abstract void cleanView(View view);
    protected abstract Object getItemFromDB(SQLiteDatabase database, long mItemId);
    protected abstract int layout();

    public interface FormListener {
        void cancel();
        void backToDetails(long mItemId);
    }
}
