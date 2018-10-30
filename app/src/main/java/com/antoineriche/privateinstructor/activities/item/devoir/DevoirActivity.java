package com.antoineriche.privateinstructor.activities.item.devoir;

import android.support.v4.app.Fragment;

import com.antoineriche.privateinstructor.activities.item.AbstractItemActivity;
import com.antoineriche.privateinstructor.activities.item.course.CourseDetailsFragment;
import com.antoineriche.privateinstructor.beans.Devoir;
import com.antoineriche.privateinstructor.database.DevoirTable;

public class DevoirActivity extends AbstractItemActivity {

    @Override
    protected Fragment getEditionFragment(long pItemId) {
        return DevoirFormFragment.newInstance(pItemId);
    }

    @Override
    protected Fragment getDetailsFragment(long pItemId) {
        return DevoirDetailsFragment.newInstance(pItemId);
    }

    @Override
    protected Fragment getAddingFragment() {
        return DevoirFormFragment.newInstance();
    }

    @Override
    protected long insertItemInDatabase(Object pItem) {
        return DevoirTable.insertDevoir(getDatabase(), (Devoir) pItem);
    }

    @Override
    protected void updateItemInDatabase(long mItemId, Object pItem) {
        DevoirTable.updateDevoir(getDatabase(), mItemId, (Devoir) pItem);
    }

    @Override
    protected boolean removeItemFromDatabase(long mItemId) {
        return DevoirTable.removeDevoirWithID(getDatabase(), mItemId);
    }

    @Override
    public Devoir getItemFromDb(long pId) {
        return DevoirTable.getDevoirWithId(getDatabase(), pId);
    }
}
