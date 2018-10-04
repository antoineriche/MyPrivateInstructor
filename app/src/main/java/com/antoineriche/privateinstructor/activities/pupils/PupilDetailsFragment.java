package com.antoineriche.privateinstructor.activities.pupils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.antoineriche.privateinstructor.DatabaseListener;
import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.activities.AbstractDatabaseFragment;
import com.antoineriche.privateinstructor.beans.Course;
import com.antoineriche.privateinstructor.beans.Pupil;
import com.antoineriche.privateinstructor.database.PupilTable;

import java.util.Comparator;
import java.util.Locale;

public class PupilDetailsFragment extends AbstractDatabaseFragment {

    private long mPupilId;
    private DetailsListener mDetailsListener;

    public PupilDetailsFragment() {
    }

    public static PupilDetailsFragment newInstance(long pPupilId) {
        PupilDetailsFragment fragment = new PupilDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(PupilActivity.ARG_PUPIL_ID, pPupilId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mPupilId = getArguments().getLong(PupilActivity.ARG_PUPIL_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pupil_fragment_details, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Pupil pupil = PupilTable.getPupilWithId(mListener.getDatabase(), mPupilId);
        ((TextView) getView().findViewById(R.id.tv_pupil_detail)).setText(pupil.toString());
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

        Drawable drawable = menu.findItem(R.id.action_edit).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(getActivity(), R.color.white));

        drawable = menu.findItem(R.id.action_delete).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(getActivity(), R.color.white));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.action_delete) {
            deletePupil(mPupilId);
        } else if (item.getItemId() == R.id.action_edit) {
            mDetailsListener.editItem(mPupilId);
        }

        return true;
    }

    private void deletePupil(long pPupilId){
        if(PupilTable.removePupilWithID(mListener.getDatabase(), pPupilId)){
            mDetailsListener.itemDeleted();
        } else {
            Toast.makeText(getContext(), String.format(Locale.FRANCE, "Pupil (%d) could not be removed", pPupilId), Toast.LENGTH_SHORT).show();
        }
    }

    public interface DetailsListener {
        void itemDeleted();
        void editItem(long mItemId);
    }
}
