package com.antoineriche.privateinstructor.activities.item;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.antoineriche.privateinstructor.DatabaseItemListener;
import com.antoineriche.privateinstructor.R;

public abstract class AbstractDetailsItemFragment extends AbstractDatabaseFragment {

    private long mItemId;
    private DetailsListener mDetailsListener;
    private DatabaseItemListener mDbItemListener;

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

        Object item = mDbItemListener.getItemFromDb(mItemId);
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

        if (context instanceof DatabaseItemListener) {
            mDbItemListener = (DatabaseItemListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement DatabaseItemListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDetailsListener = null;
        mDbItemListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.details_item, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.action_delete) {
            openDeletionDialog(mItemId).show();
        } else if (item.getItemId() == R.id.action_edit) {
            mDetailsListener.setIdForEditFragment(mItemId);
        }

        return true;
    }

    private AlertDialog.Builder openDeletionDialog(long pId){
        Drawable drawable = getContext().getDrawable(R.drawable.baseline_warning_white_48);
        drawable.setTint(getContext().getColor(R.color.unthem500));
        return new AlertDialog.Builder(getContext())
                .setIcon(drawable)
                .setTitle("Attention")
                .setMessage(deletionDialogMessage())
                .setNegativeButton("Non", null)
                .setPositiveButton("Oui", (dialog, which) -> mDbItemListener.removeItem(pId));
    }

    protected abstract int layout();
    protected abstract void fillViewWithItem(View pView, Object pItem);
    protected abstract String deletionDialogMessage();

    protected DatabaseItemListener getDbItemListener() {
        return mDbItemListener;
    }

    protected Object getItem(){
        return getDbItemListener().getItemFromDb(getItemId());
    }

    public long getItemId() {
        return mItemId;
    }

    public interface DetailsListener {
        void setIdForEditFragment(long mItemId);
    }

}
