package com.antoineriche.privateinstructor.activities.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.customviews.SwitchPreferences;
import com.antoineriche.privateinstructor.utils.PreferencesUtils;

public class SettingsNotificationFragment extends Fragment {

    String TAG = SettingsNotificationFragment.class.getSimpleName();

    public SettingsNotificationFragment() {
    }

    public static SettingsNotificationFragment newInstance() {
        return new SettingsNotificationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = PreferencesUtils.getDefaultSharedPreferences(getContext());
        ((SwitchPreferences) view.findViewById(R.id.swipref_course_beginning)).setSharedPreferences(sharedPreferences);
        ((SwitchPreferences) view.findViewById(R.id.swipref_course_end)).setSharedPreferences(sharedPreferences);
    }
}
