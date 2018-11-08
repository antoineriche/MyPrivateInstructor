package com.antoineriche.privateinstructor.activities.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.customviews.MyTabLayout;
import com.antoineriche.privateinstructor.utils.PreferencesUtils;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsFragment extends Fragment {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    public final static String TAB_GENERAL = "general";
    public final static String TAB_NOTIFICATIONS = "notifications";
    public final static String TAB_FIREBASE = "firebase";


    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MyTabLayout mtl = view.findViewById(R.id.mtl_settings);
        mtl.addTab("Général", TAB_GENERAL);
        mtl.addTab("Notifications", TAB_NOTIFICATIONS);
        mtl.addTab("Firebase", TAB_FIREBASE);
        mtl.addTabListener(tabTag -> {
            if (TAB_GENERAL.equals(tabTag)) {
                loadFragment(SettingsGeneralFragment.newInstance());
            } else if (TAB_FIREBASE.equals(tabTag)){
                loadFragment(SettingsAccountFragment.newInstance());
            } else if (TAB_NOTIFICATIONS.equals(tabTag)){
                loadFragment(SettingsNotificationFragment.newInstance());
            }
        });
        mtl.getTabAt(0);


//FIXME
//        SharedPreferences sharedPreferences = PreferencesUtils.getDefaultSharedPreferences(getActivity());

//        RadioGroup radioGroup = view.findViewById(R.id.rg_sync_freq);
//        enableRadioGroup(radioGroup, PreferencesUtils.getBooleanPreferences(getContext(),
//                getString(R.string.pref_enable_firebase_snapshot)));
//        radioGroup.setOnCheckedChangeListener((radioGroup1, childId) -> {
//            long snapshotFreq = 0;
//            switch(childId) {
//                case R.id.rb_sync_daily:
//                    snapshotFreq = DateUtils.DAY;
//                    break;
//                case R.id.rb_sync_weekly:
//                    snapshotFreq = DateUtils.DAY * 7;
//                    break;
//                case R.id.rb_sync_monthly:
//                    snapshotFreq = DateUtils.DAY * 30;
//                    break;
//            }
//            PreferencesUtils.setLongPreferences(getContext(),
//                    getString(R.string.pref_firebase_snapshot_frequency), snapshotFreq);
//        });
//
//        if(PreferencesUtils.getBooleanPreferences(getContext(), getString(R.string.pref_enable_firebase_snapshot))){
//            ((RadioButton) view.findViewById(getCheckedRadioButtonId(getContext())))
//                    .setChecked(true);
//        }

        //FIXME
//        ((SwitchPreferences) view.findViewById(R.id.swipref_course_beginning)).setSharedPreferences(sharedPreferences);
//        ((SwitchPreferences) view.findViewById(R.id.swipref_course_end)).setSharedPreferences(sharedPreferences);
//
//        ((SwitchPreferences) view.findViewById(R.id.swipref_enable_firebase_synchronization)).setSharedPreferences(sharedPreferences);
//        ((SwitchPreferences) view.findViewById(R.id.swipref_enable_firebase_snapshots)).setSharedPreferences(sharedPreferences);



//        SwitchPreferences switchFirebase = view.findViewById(R.id.swipref_enable_firebase_snapshots);
//        switchFirebase.setSharedPreferences(sharedPreferences);
//        switchFirebase.setSwitchPreferencesListener(new SwitchPreferences.SwitchPreferencesListener() {
//            @Override
//            public void isChecked(boolean isChecked) {
//                enableRadioGroup(radioGroup, isChecked);
//                if(isChecked){
//                    Log.e(TAG, "Current radioId: " + getCheckedRadioButtonId(getContext()));
//                    ((RadioButton) view.findViewById(getCheckedRadioButtonId(getContext())))
//                            .setChecked(true);
//                }
//            }
//
//            @Override
//            public void preferencesUpdated(String pKey, boolean pValue) {
//            }
//
//            @Override
//            public void updateError(String pError, String pKey) {
//            }
//        });

//        view.findViewById(R.id.btn_create_snapshot).setOnClickListener(v -> {
//            view.findViewById(R.id.btn_create_snapshot).setEnabled(false);
//            mBoundService.createSnapshot(mActivityMessenger);
//        });

//        view.findViewById(R.id.btn_import_courses).setOnClickListener(v -> {
//            view.findViewById(R.id.btn_import_courses).setEnabled(false);
//            mBoundService.retrieveOldSnapshot(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    String key = null;
//                    for (DataSnapshot snap : dataSnapshot.getChildren()){
//                        Log.e(TAG, "Snaphot: " + snap.getKey());
//                        if(key == null){
//                            key = snap.getKey();
//                        }
//                    }
//                    Log.e(TAG, "Found " + dataSnapshot.getChildrenCount() + " snapshots");
//
//                    if(key != null){
//                        mBoundService.replaceCurrentDBWithSnapshot(dataSnapshot.child(key));
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        });

//        view.findViewById(R.id.btn_firebase_save).setEnabled(false);
//        view.findViewById(R.id.btn_firebase_save).setOnClickListener(v -> {
//            view.findViewById(R.id.btn_firebase_save).setEnabled(false);
//            mBoundService.synchronizeRemoteDataBase(mActivityMessenger);
//        });

    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        Intent intent = new Intent(context, FirebaseService.class);
//        context.startService(intent);
//        context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (mServiceConnected) {
//            getActivity().unbindService(mServiceConnection);
//            mServiceConnected = false;
//        }
//    }

//    private void updateCount(int textViewResId, int pCount){
//        ((TextView) getView().findViewById(textViewResId)).setText(String.valueOf(pCount));
//    }
//
//    private void updateViewVisibility(int viewResId, boolean pVisible){
//        getView().findViewById(viewResId).setVisibility(pVisible ? View.VISIBLE : View.GONE);
//    }
//
//    private void enableView(int viewResId, boolean pEnable){
//        getView().findViewById(viewResId).setEnabled(pEnable);
//    }
//
//    private void updateSyncState(String pStateMsg){
//        ((TextView)getView().findViewById(R.id.tv_synchronization_state)).setText(pStateMsg);
//    }


//    private void coursesSuccessfullySynchronized(){
//        updateViewVisibility(R.id.pb_check, false);
//        updateCount(R.id.tv_course_to_add_count,0);
//        updateCount(R.id.tv_course_to_remove_count,0);
//        updateCount(R.id.tv_course_to_update_count,0);
//        updateSyncState("Les bases sont synchronisées.");
//    }

//    private void enableRadioGroup(RadioGroup radioGroup, boolean enabled){
//        for(int i = 0 ; i < radioGroup.getChildCount() ; i++){
//            radioGroup.getChildAt(i).setEnabled(enabled);
//            if(!enabled){
//                ((RadioButton)radioGroup.getChildAt(i)).setChecked(false);
//            }
//        }
//    }
//
//    private int getCheckedRadioButtonId(Context pContext){
//        if ( PreferencesUtils.getLongPreferences(pContext,
//                getString(R.string.pref_firebase_snapshot_frequency)) == DateUtils.DAY){
//            return R.id.rb_sync_daily;
//        } else if ( PreferencesUtils.getLongPreferences(pContext,
//                getString(R.string.pref_firebase_snapshot_frequency)) == DateUtils.DAY * 7){
//            return R.id.rb_sync_weekly;
//        } else if ( PreferencesUtils.getLongPreferences(pContext,
//                getString(R.string.pref_firebase_snapshot_frequency)) == DateUtils.DAY * 30){
//            return R.id.rb_sync_monthly;
//        } else {
//            return -1;
//        }
//    }

    private void loadFragment(Fragment pFragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fl_settings, pFragment).commit();
    }

}
