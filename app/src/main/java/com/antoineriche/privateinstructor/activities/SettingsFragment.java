package com.antoineriche.privateinstructor.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.customviews.SwitchPreferences;
import com.antoineriche.privateinstructor.utils.DateUtils;
import com.antoineriche.privateinstructor.utils.FirebaseUtils;
import com.antoineriche.privateinstructor.utils.PreferencesUtils;
import com.antoineriche.privateinstructor.utils.SnapshotFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class SettingsFragment extends Fragment {

    String TAG = SettingsFragment.class.getSimpleName();

//    private Messenger mBoundServiceMessenger;
//    private FirebaseService mBoundService;
//    private boolean mServiceConnected = false;
//    private final Messenger mActivityMessenger = new Messenger(new MyHandler(this));
//    private ServiceConnection mServiceConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            Log.e(TAG, "onServiceDisconnected");
//            mBoundService = null;
//            mBoundServiceMessenger = null;
//            mServiceConnected = false;
//        }
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            Log.e(TAG, "onServiceConnected");
//            mBoundServiceMessenger = ((FirebaseService.LocalBinder) service).getMessenger();
//            mBoundService = ((FirebaseService.LocalBinder) service).getService();
//            mServiceConnected = true;
//            mBoundService.checkSyncState(mActivityMessenger);
//        }
//    };

//    static class MyHandler extends Handler {
//        private final WeakReference<SettingsFragment> mSettingsFragment;
//
//        MyHandler(SettingsFragment fragment) {
//            mSettingsFragment = new WeakReference<>(fragment);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case FirebaseService.MSG_SYNC_CHECK_STATE:
//                    boolean isUpToDate = msg.getData().getBoolean(FirebaseService.MSG_EXTRA_DATA);
//                    mSettingsFragment.get().enableView(R.id.btn_firebase_save, !isUpToDate);
//                    String str = isUpToDate ? "Les bases sont synchronisées" : "Les bases ne sont pas synchronisées.";
//                    mSettingsFragment.get().updateSyncState(str);
//                    break;
//                case FirebaseService.MSG_SYNC_CHECK_START:
//                    mSettingsFragment.get().updateViewVisibility(R.id.pb_check, true);
//                    mSettingsFragment.get().updateSyncState("Contrôle de la base distante.");
//                    break;
//                case FirebaseService.MSG_SYNC_CHECK_END:
//                    mSettingsFragment.get().updateViewVisibility(R.id.pb_check, false);
//                    break;
//                case FirebaseService.MSG_SYNC_CHECK_COURSE_TO_ADD_COUNT:
//                    mSettingsFragment.get().updateCount(R.id.tv_course_to_add_count,
//                            msg.getData().getInt(FirebaseService.MSG_EXTRA_DATA));
//                    break;
//                case FirebaseService.MSG_SYNC_CHECK_COURSE_TO_REMOVE_COUNT:
//                    mSettingsFragment.get().updateCount(R.id.tv_course_to_remove_count,
//                            msg.getData().getInt(FirebaseService.MSG_EXTRA_DATA));
//                    break;
//                case FirebaseService.MSG_SYNC_CHECK_COURSE_TO_UPDATE_COUNT:
//                    mSettingsFragment.get().updateCount(R.id.tv_course_to_update_count,
//                            msg.getData().getInt(FirebaseService.MSG_EXTRA_DATA));
//                    break;
//
//                case FirebaseService.MSG_SYNCHRONIZATION_START:
//                    mSettingsFragment.get().updateViewVisibility(R.id.pb_check, true);
//                    mSettingsFragment.get().updateSyncState("Synchronisation en cours.");
//                    break;
//                case FirebaseService.MSG_SYNCHRONIZATION_SUCCESS:
//                    mSettingsFragment.get().coursesSuccessfullySynchronized();
//                    mSettingsFragment.get().updateSyncState("Les bases sont synchronisées");
//                    break;
//                case FirebaseService.MSG_SYNCHRONIZATION_ERROR:
//                    str = String.format(Locale.FRANCE, "Erreur lors de la synchronisation\n%s",
//                            msg.getData().getString(FirebaseService.MSG_EXTRA_DATA));
//                    mSettingsFragment.get().updateSyncState(str);
//                    mSettingsFragment.get().updateViewVisibility(R.id.pb_check, false);
//                    break;
//
//                case FirebaseService.MSG_SNAPSHOT_START:
//                    mSettingsFragment.get().updateViewVisibility(R.id.pb_check, true);
//                    mSettingsFragment.get().updateSyncState("Snapshot creation starts");
//                    break;
//                case FirebaseService.MSG_SNAPSHOT_ERROR:
//                    str = String.format(Locale.FRANCE, "Erreur de snapshot\n%s",
//                            msg.getData().getString(FirebaseService.MSG_EXTRA_DATA));
//                    mSettingsFragment.get().updateSyncState(str);
//                    mSettingsFragment.get().enableView(R.id.btn_create_snapshot, true);
//                    mSettingsFragment.get().updateViewVisibility(R.id.pb_check, false);
//                    break;
//                case FirebaseService.MSG_SNAPSHOT_SUCCESS:
//                    str = String.format(Locale.FRANCE, "Snapshot created\n%s",
//                            msg.getData().getString(FirebaseService.MSG_EXTRA_DATA));
//                    mSettingsFragment.get().updateSyncState(str);
//                    mSettingsFragment.get().enableView(R.id.btn_create_snapshot, true);
//                    mSettingsFragment.get().updateViewVisibility(R.id.pb_check, false);
//                    break;
//
//            }
//        }
//    }

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
        SharedPreferences sharedPreferences = PreferencesUtils.getDefaultSharedPreferences(getActivity());

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

        ((SwitchPreferences) view.findViewById(R.id.swipref_course_beginning)).setSharedPreferences(sharedPreferences);
        ((SwitchPreferences) view.findViewById(R.id.swipref_course_end)).setSharedPreferences(sharedPreferences);

        ((SwitchPreferences) view.findViewById(R.id.swipref_enable_firebase_synchronization)).setSharedPreferences(sharedPreferences);
        ((SwitchPreferences) view.findViewById(R.id.swipref_enable_firebase_snapshots)).setSharedPreferences(sharedPreferences);
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("Paramètres");
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
}
