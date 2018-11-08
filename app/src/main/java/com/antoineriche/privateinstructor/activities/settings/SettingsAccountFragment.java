package com.antoineriche.privateinstructor.activities.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.customviews.SwitchPreferences;
import com.antoineriche.privateinstructor.utils.PreferencesUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsAccountFragment extends Fragment {

    private static final String TAG = SettingsAccountFragment.class.getSimpleName();
    private FirebaseAuth mAuth;
    private SharedPreferences mSPreferences;

    public SettingsAccountFragment() {
    }

    public static SettingsAccountFragment newInstance() {
        return new SettingsAccountFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(getContext());
        mAuth = FirebaseAuth.getInstance();
        mSPreferences = PreferencesUtils.getDefaultSharedPreferences(getContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        fillView(getView(), user);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((SwitchPreferences) view.findViewById(R.id.swipref_enable_firebase_synchronization)).setSharedPreferences(mSPreferences);
        ((SwitchPreferences) view.findViewById(R.id.swipref_enable_firebase_snapshots)).setSharedPreferences(mSPreferences);
    }

    private void fillView(View view, FirebaseUser user){
        ((TextView) view.findViewById(R.id.tv_user_mail)).setText( user != null ?  user.getEmail() : "Unknown");
        ((TextView) view.findViewById(R.id.tv_user_firebase_id)).setText( user != null ?  user.getUid() : "Unknown");

        if(user != null && !TextUtils.isEmpty(user.getEmail())) {
            view.findViewById(R.id.tv_user_reset_password).setOnClickListener(v -> {
                mAuth.sendPasswordResetEmail(user.getEmail()).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.e(TAG, "Password sent to " + user.getEmail());
                    } else {
                        Log.e(TAG, "Can not send mail to " + user.getEmail(), task.getException());
                    }
                });
            });
        } else {
            view.findViewById(R.id.tv_user_reset_password).setEnabled(false);
        }

        view.findViewById(R.id.tv_user_reset_password).setEnabled(false);
    }
}
