package com.antoineriche.privateinstructor.activities.item.pupil;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.activities.item.AbstractDetailsItemFragment;
import com.antoineriche.privateinstructor.activities.item.AbstractItemActivity;
import com.antoineriche.privateinstructor.beans.Pupil;
import com.antoineriche.privateinstructor.database.PupilTable;

public class PupilDetailsFragment extends AbstractDetailsItemFragment {

    public PupilDetailsFragment() {
    }

    public static PupilDetailsFragment newInstance(long pCourseId) {
        PupilDetailsFragment fragment = new PupilDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(AbstractItemActivity.ARG_ITEM_ID, pCourseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected Pupil getItemFromDB(SQLiteDatabase pDatabase, long pId) {
        return PupilTable.getPupilWithId(pDatabase, pId);
    }

    @Override
    protected boolean deleteItemFromDB(SQLiteDatabase pDatabase, long pId) {
        return PupilTable.removePupilWithID(pDatabase, pId);
    }

    @Override
    protected int layout() {
        return R.layout.pupil_details;
    }

    @Override
    protected void fillViewWithItem(View pView, Object pItem) {
        Pupil pupil = (Pupil) pItem;
        ((TextView) pView.findViewById(R.id.tv_pupil_detail)).setText(pupil.toString());
    }
}
