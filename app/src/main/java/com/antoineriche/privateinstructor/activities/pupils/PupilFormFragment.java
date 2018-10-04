package com.antoineriche.privateinstructor.activities.pupils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.activities.AbstractDatabaseFragment;
import com.antoineriche.privateinstructor.beans.Pupil;
import com.antoineriche.privateinstructor.database.PupilTable;

import java.util.Locale;

public class PupilFormFragment extends AbstractDatabaseFragment {

    private long mPupilId;
    private boolean isEditing;
    private FormListener mFormListener;

    public PupilFormFragment() {
    }

    public static PupilFormFragment newInstance(long pPupilId) {
        PupilFormFragment fragment = new PupilFormFragment();
        Bundle args = new Bundle();
        args.putLong(PupilActivity.ARG_PUPIL_ID, pPupilId);
        args.putBoolean(PupilActivity.ARG_PUPIL_EDITION, true);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mPupilId = getArguments().getLong(PupilActivity.ARG_PUPIL_ID);
            isEditing = getArguments().getBoolean(PupilActivity.ARG_PUPIL_EDITION, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pupil_fragment_form, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(isEditing){
            Pupil pupil = PupilTable.getPupilWithId(mListener.getDatabase(), mPupilId);
            ((EditText) getView().findViewById(R.id.et_pupil_firstname)).setText(pupil.getFirstname());
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFormListener = null;
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
                mFormListener.backToDetails(mPupilId);
            } else {
                mFormListener.cancel();
            }
        } else if (item.getItemId() == R.id.action_save) {
            Pupil p = PupilTable.getPupilWithId(mListener.getDatabase(), mPupilId);
            p.setFirstname(((EditText)getView().findViewById(R.id.et_pupil_firstname)).getText().toString());
            mFormListener.saveItem(mPupilId, p);
        }

        return true;
    }


    public interface FormListener {
        void cancel();
        void backToDetails(long mItemId);
        void saveItem(long mItemId, Pupil pPupil);
    }
}
