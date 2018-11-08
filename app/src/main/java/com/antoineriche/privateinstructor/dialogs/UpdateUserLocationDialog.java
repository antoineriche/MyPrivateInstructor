package com.antoineriche.privateinstructor.dialogs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.beans.Location;
import com.antoineriche.privateinstructor.database.LocationTable;
import com.antoineriche.privateinstructor.database.MyDatabase;
import com.antoineriche.privateinstructor.utils.FirebaseUtils;
import com.antoineriche.privateinstructor.utils.PreferencesUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateUserLocationDialog extends MyCustomDialog {

    private static final String TAG = UpdateUserLocationDialog.class.getSimpleName();

    private final Location mLocation;
    private final String mUserUid;
    private UpdateLocationListener mListener;

    public UpdateUserLocationDialog(Context context, @NonNull Location pLocation, @NonNull String pUserUid, @NonNull UpdateLocationListener pListener) {
        super(context);
        this.mLocation = pLocation;
        this.mUserUid = pUserUid;
        this.mListener = pListener;
    }

    @Override
    protected int layoutView() {
        return R.layout.dialog_default_textview;
    }

    @Override
    protected String title() { return "Mise à jour"; }

    @Override
    protected void positiveClick() {
        setProgressing(true);
        hideError();

        String locationUuid = PreferencesUtils.getStringPreferences(getContext(), getContext().getString(R.string.pref_user_location));

        SQLiteDatabase sqLite = new MyDatabase(getContext(), null).getWritableDatabase();

//        Intent intent = new Intent(getContext(), DatabaseIntentService.class);
//        DatabaseIntentService.DatabaseOperation.Operation operation;

        //1. Save in local
        if(TextUtils.isEmpty(locationUuid)) {
//            operation = DatabaseIntentService.DatabaseOperation.Operation.INSERT;
            locationUuid = LocationTable.insertLocation(sqLite, mLocation);
            Log.e(TAG, "Insert in SQLite");
        } else {
//            operation = DatabaseIntentService.DatabaseOperation.Operation.UPDATE;
            mLocation.setUuid(locationUuid);
            LocationTable.updateLocationWithUuid(sqLite, locationUuid, mLocation);
            Log.e(TAG, "Update in SQLite");
        }
        Log.e(TAG, "Successfully saved in SQLite.");

//        intent.putExtra(DatabaseIntentService.DB_EXTRAS,
//                DatabaseIntentService.DatabaseOperation.toBundle(
//                        operation, userLocation, Location.class));
//
//        getActivity().startService(intent);

        //2. Save in preferences
        PreferencesUtils.setStringPreferences(getContext(), getContext().getString(R.string.pref_user_location), locationUuid);
        Log.e(TAG, "Successfully saved in preferences.");

        //3. Save in remote location table
        FirebaseUtils.updateUserLocation(mUserUid, locationUuid,
                o -> {
                    Log.e(TAG, "Successfully saved in Firebase.");
                    mListener.onLocationUpdated();
                    dismiss();
                },
                e -> printError(e.getMessage())
        );

    }

    @Override
    protected void negativeClick() {
        mListener.onCancelUpdate();
        dismiss();
    }

    @Override
    protected void neutralClick() {
        Toast.makeText(getContext(), "Neutral", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TextView) findViewById(R.id.tv_dialog_text)).setText("L'adresse a été modifiée, souhaitez-vous conserver les modifications ?");
    }

    public interface UpdateLocationListener {
        void onLocationUpdated();
        void onCancelUpdate();
    }
}
