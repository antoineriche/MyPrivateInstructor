package com.antoineriche.privateinstructor.activities.item.pupil;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.activities.item.AbstractFormItemFragment;
import com.antoineriche.privateinstructor.activities.item.AbstractItemActivity;
import com.antoineriche.privateinstructor.beans.Pupil;
import com.antoineriche.privateinstructor.database.PupilTable;

public class PupilFormFragment extends AbstractFormItemFragment {

    public PupilFormFragment() {
    }

    public static PupilFormFragment newInstance(long pPupilId) {
        PupilFormFragment fragment = new PupilFormFragment();
        Bundle args = new Bundle();
        args.putLong(AbstractItemActivity.ARG_ITEM_ID, pPupilId);
        args.putBoolean(AbstractItemActivity.ARG_ITEM_EDITION, true);
        fragment.setArguments(args);
        return fragment;
    }

    public static PupilFormFragment newInstance() {
        return new PupilFormFragment();
    }

    @Override
    protected Pupil extractItemFromView(View view) throws IllegalArgumentException {
        Pupil p;
        //FIXME
        if(mItemId != -1) {
            p = getItemFromDB(mListener.getDatabase(), mItemId);
        } else {
            p = new Pupil("firstname", "lastname", 2, Pupil.BLACK, Pupil.GENDER_MALE, Pupil.OCCASIONALY, "anywhere", 25);
        }
        p.setFirstname(((EditText) view.findViewById(R.id.et_pupil_first_name)).getText().toString());
        return p;
    }

    @Override
    protected void fillViewWithItem(View view, Object item) {
        Pupil pupil = (Pupil) item;
        ((EditText) view.findViewById(R.id.et_pupil_first_name)).setText(pupil.getFirstname());
    }

    @Override
    protected void cleanView(View view) {
        ((EditText) view.findViewById(R.id.et_pupil_first_name)).setText(null);
    }

    @Override
    protected Pupil getItemFromDB(SQLiteDatabase database, long mItemId) {
        return PupilTable.getPupilWithId(database, mItemId);
    }

    @Override
    protected int layout() {
        return R.layout.pupil_form;
    }

}
