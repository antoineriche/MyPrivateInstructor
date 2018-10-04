package com.antoineriche.privateinstructor.activities;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.antoineriche.privateinstructor.DatabaseListener;

public class AbstractDatabaseFragment extends Fragment {

    protected DatabaseListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DatabaseListener) {
            mListener = (DatabaseListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement DatabaseListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
