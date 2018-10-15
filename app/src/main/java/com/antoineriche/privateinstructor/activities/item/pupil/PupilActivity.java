package com.antoineriche.privateinstructor.activities.item.pupil;

import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;

import com.antoineriche.privateinstructor.activities.item.AbstractItemActivity;
import com.antoineriche.privateinstructor.beans.Pupil;
import com.antoineriche.privateinstructor.database.PupilTable;

public class PupilActivity extends AbstractItemActivity {

    @Override
    protected Fragment getEditionFragment(long pItemId) {
        return PupilFormFragment.newInstance(pItemId);
    }

    @Override
    protected Fragment getDetailsFragment(long pItemId) {
        return PupilDetailsFragment.newInstance(pItemId);
    }

    @Override
    protected Fragment getAddingFragment() {
        return PupilFormFragment.newInstance();
    }

    @Override
    protected long insertItemInDatabase(SQLiteDatabase pDatabase, Object pItem) {
        return PupilTable.insertPupil(getDatabase(), (Pupil) pItem);
    }

    @Override
    protected void updateItemInDatabase(SQLiteDatabase pDatabase, long mItemId, Object pItem) {
        PupilTable.updatePupil(getDatabase(), mItemId, (Pupil) pItem);
    }

}
