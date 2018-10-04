package com.antoineriche.privateinstructor.activities.pupils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.activities.AbstractDatabaseActivity;
import com.antoineriche.privateinstructor.activities.ToImplementFragment;
import com.antoineriche.privateinstructor.beans.Pupil;
import com.antoineriche.privateinstructor.database.PupilTable;

public class PupilActivity extends AbstractDatabaseActivity implements PupilDetailsFragment.DetailsListener,
        PupilFormFragment.FormListener {

    public static final String ARG_PUPIL_ID = "pupil-id";
    public static final String ARG_PUPIL_EDITION = "pupil-edition";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pupil_activity);

        Fragment fragment = getFragmentFromBundle(getIntent().getExtras());
        loadFragment(fragment);

        if(fragment instanceof ToImplementFragment){
            ((TextView)findViewById(R.id.tv_pupil_txt)).setText("ID: "+getIntent().getExtras().getInt(ARG_PUPIL_ID));
        } else {
            findViewById(R.id.tv_pupil_txt).setVisibility(View.GONE);
        }

        // USE TO ADD PUPIL
//        findViewById(R.id.fab_add_pupil).setOnClickListener(v -> {
//            PupilTable.insertPupil(getDatabase(),
//                    new Pupil("firstname", "lastname", 2, Pupil.BLACK, Pupil.GENDER_MALE, Pupil.OCCASIONALY, "anywhere", 25)
//            );
//            Toast.makeText(this, "Pupil added", Toast.LENGTH_SHORT).show();
//        });
    }

    @Override
    protected boolean isDatabaseWritable() {
        return true;
    }

    private Fragment getFragmentFromBundle(Bundle pBundle){
        Fragment fragment;
        if(pBundle == null){
            fragment = ToImplementFragment.newInstance("Pupil");
        } else {
            long pupilId = pBundle.getLong(ARG_PUPIL_ID, -1);

            if(pupilId != -1){
                if(pBundle.getBoolean(ARG_PUPIL_EDITION)){
                    fragment = PupilFormFragment.newInstance(pupilId);
                } else {
                    fragment = PupilDetailsFragment.newInstance(pupilId);
                }
            } else {
                fragment = ToImplementFragment.newInstance("Pupil");
            }
        }
        return fragment;
    }

    private void loadFragment(Fragment pFragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fl_pupil_content, pFragment).commit();
    }

    @Override
    public void itemDeleted() {
        finish();
    }

    @Override
    public void editItem(long mItemId) {
        Bundle args = new Bundle();
        args.putLong(ARG_PUPIL_ID, mItemId);
        args.putBoolean(ARG_PUPIL_EDITION, true);
        Fragment fragment = getFragmentFromBundle(args);
        loadFragment(fragment);
    }

    @Override
    public void backToDetails(long mItemId) {
        Bundle args = new Bundle();
        args.putLong(ARG_PUPIL_ID, mItemId);
        Fragment fragment = getFragmentFromBundle(args);
        loadFragment(fragment);
    }


    @Override
    public void cancel() {
        finish();
    }

    @Override
    public void saveItem(long mItemId, Pupil pPupil) {

        PupilTable.updatePupil(getDatabase(), mItemId, pPupil);

        Bundle args = new Bundle();
        args.putLong(ARG_PUPIL_ID, mItemId);
        Fragment fragment = getFragmentFromBundle(args);
        loadFragment(fragment);
    }
}
