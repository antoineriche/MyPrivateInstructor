package com.antoineriche.privateinstructor.activities.item;

import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.database.PupilTable;

import java.util.Locale;

public abstract class AbstractDetailsItemFragment extends AbstractDatabaseFragment {

    private long mItemId;
    private DetailsListener mDetailsListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mItemId = getArguments().getLong(AbstractItemActivity.ARG_ITEM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(layout(), container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Object item = getItemFromDB(mListener.getDatabase(), mItemId);
        fillViewWithItem(getView(), item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DetailsListener) {
            mDetailsListener = (DetailsListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement DetailsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDetailsListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.details_item, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.action_delete) {
            openDeletionDialog(mListener.getDatabase(), mItemId).show();
        } else if (item.getItemId() == R.id.action_edit) {
            mDetailsListener.editItem(mItemId);
        }

        return true;
    }

    private void deleteItem(SQLiteDatabase pDatabase, long pId){

        if(deleteItemFromDB(pDatabase, pId)){
            mDetailsListener.itemDeleted();
        } else {
            Toast.makeText(getContext(), String.format(Locale.FRANCE, "Item (%d) could not be removed", pId), Toast.LENGTH_SHORT).show();
        }
    }

    private AlertDialog.Builder openDeletionDialog(SQLiteDatabase pDatabase, long pId){
        Drawable drawable = getContext().getDrawable(R.drawable.baseline_warning_white_48);
        drawable.setTint(getContext().getColor(R.color.unthem500));
        return new AlertDialog.Builder(getContext())
                .setIcon(drawable)
                .setTitle("Attention")
                .setMessage(deletionDialogMessage())
                .setNegativeButton("Non", null)
                .setPositiveButton("Oui", (dialog, which) -> deleteItem(pDatabase, pId));
    }


    protected abstract Object getItemFromDB(SQLiteDatabase pDatabase, long pId);
    protected abstract boolean deleteItemFromDB(SQLiteDatabase pDatabase, long pId);
    protected abstract int layout();
    protected abstract void fillViewWithItem(View pView, Object pItem);
    protected abstract String deletionDialogMessage();

    public interface DetailsListener {
        void itemDeleted();
        void editItem(long mItemId);
    }

}
