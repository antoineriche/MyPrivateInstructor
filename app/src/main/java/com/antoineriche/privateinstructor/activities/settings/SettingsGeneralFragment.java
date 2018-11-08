package com.antoineriche.privateinstructor.activities.settings;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import com.antoineriche.privateinstructor.beans.Location;
import com.antoineriche.privateinstructor.database.LocationTable;
import com.antoineriche.privateinstructor.database.MyDatabase;
import com.antoineriche.privateinstructor.dialogs.LogInDialog;
import com.antoineriche.privateinstructor.dialogs.UpdateUserLocationDialog;
import com.antoineriche.privateinstructor.services.DatabaseIntentService;
import com.antoineriche.privateinstructor.utils.FirebaseUtils;
import com.antoineriche.privateinstructor.utils.PreferencesUtils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class SettingsGeneralFragment extends Fragment implements UpdateUserLocationDialog.UpdateLocationListener {

    private static final String TAG = SettingsGeneralFragment.class.getSimpleName();
    private static int REQUEST_CODE_PLACE_AUTOCOMPLETE = 95;

    public SettingsGeneralFragment() {
    }

    public static SettingsGeneralFragment newInstance() {
        return new SettingsGeneralFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_general, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String locationUuid = PreferencesUtils.getStringPreferences(getContext(), getContext().getString(R.string.pref_user_location));
        SQLiteDatabase sql = new MyDatabase(getContext(), null).getReadableDatabase();
        Location location = LocationTable.getLocationWithUuid(sql, locationUuid);

        ((TextView) view.findViewById(R.id.tv_user_location)).setText( location != null  ?  location.getAddress() : "Unknown");

        view.findViewById(R.id.cv_address).setOnClickListener(v -> {
            Intent intent = null;
            try {
                intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                        .setFilter(new AutocompleteFilter.Builder().setCountry("FR").build())
                        .build(getActivity());
            } catch (GooglePlayServicesRepairableException e) {
                Log.e(TAG, "GooglePlayServicesRepairableException", e);
            } catch (GooglePlayServicesNotAvailableException e) {
                Log.e(TAG, "GooglePlayServicesNotAvailableException", e);
            }

            startActivityForResult(intent, REQUEST_CODE_PLACE_AUTOCOMPLETE);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PLACE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                TextView tvAddress = getView().findViewById(R.id.tv_user_location);
                if(tvAddress != null){ tvAddress.setText(place.getAddress()); }
                updatePlace(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                Log.e(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) { Log.e(TAG, "Error"); }
        }
    }

    private FirebaseUser mCurrentUser;

    protected boolean isConnected(){
        return mCurrentUser != null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(getContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    protected String getUserUid(){
        return isConnected() ? mCurrentUser.getUid() : null;
    }

    @Override
    public void onLocationUpdated() {
        super.onStop();
    }

    @Override
    public void onCancelUpdate() {
        super.onStop();
    }

    private void updatePlace(Place place){
        Location location = new Location(place);

        String locationUuid = PreferencesUtils.getStringPreferences(getContext(), getString(R.string.pref_user_location));
        SQLiteDatabase sqLite = new MyDatabase(getContext(), null).getWritableDatabase();

        //1. Save in local
        if(TextUtils.isEmpty(locationUuid)) {
            locationUuid = LocationTable.insertLocation(sqLite, location);
            //2. Save in preferences
            PreferencesUtils.setStringPreferences(getContext(), getString(R.string.pref_user_location), locationUuid);
            Log.e(TAG, "Successfully saved in preferences.");
            //3. Save in remote location table
            FirebaseUtils.updateUserLocation(getUserUid(), locationUuid,
                    o -> Log.e(TAG, "Successfully saved in Firebase."),
                    e -> Log.e(TAG, "Error while saving in Firebase.", e)
            );
            Log.e(TAG, "Insert in SQLite");
        } else {
            location.setUuid(locationUuid);
            LocationTable.updateLocationWithUuid(sqLite, locationUuid, location);
            Log.e(TAG, "Update in SQLite");
        }
        Log.e(TAG, "Successfully saved in SQLite.");
    }
}
