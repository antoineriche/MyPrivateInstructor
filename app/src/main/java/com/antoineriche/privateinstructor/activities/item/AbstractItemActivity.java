package com.antoineriche.privateinstructor.activities.item;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.antoineriche.privateinstructor.DatabaseItemListener;
import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.activities.ToImplementFragment;

//FIXME pb de drawer

public abstract class AbstractItemActivity extends AbstractDatabaseActivity
        implements AbstractFormItemFragment.FormListener, DatabaseItemListener,
        AbstractDetailsItemFragment.DetailsListener {

    public static final String ARG_ITEM_ID = "item-id";
    public static final String ARG_ITEM_EDITION = "item-edition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        Fragment fragment = getFragmentFromBundle(getIntent().getExtras());
        loadFragment(fragment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(null);
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
    protected abstract long insertItemInDatabase(Object pItem);
    protected abstract void updateItemInDatabase(long mItemId, Object pItem);
    protected abstract boolean removeItemFromDatabase(long mItemId);

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


    /**
     * DetailsListener
     */
    @Override
    public void setIdForEditFragment(long mItemId) {
        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, mItemId);
        args.putBoolean(ARG_ITEM_EDITION, true);
        Fragment fragment = getFragmentFromBundle(args);
        loadFragment(fragment);
    }


    /**
     * FormListener
     */
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


    /**
     * DatabaseItemListener
     */
    @Override
    public void updateItem(long mItemId, Object pNewItem) {
        updateItemInDatabase(mItemId, pNewItem);
        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, mItemId);
        Fragment fragment = getFragmentFromBundle(args);
        loadFragment(fragment);
    }

    @Override
    public void saveItem(Object pNewItem) {
        long pId = insertItemInDatabase(pNewItem);

        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, pId);
        Fragment fragment = getFragmentFromBundle(args);
        loadFragment(fragment);
    }

    @Override
    public void removeItem(long mItemId) {
        if(removeItemFromDatabase(mItemId)) {
            finish();
        } else {
            Log.e(getClass().getSimpleName(), "Error while removing");
        }
    }

}
