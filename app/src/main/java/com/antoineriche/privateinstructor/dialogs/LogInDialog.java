package com.antoineriche.privateinstructor.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.utils.PreferencesUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInDialog extends MyCustomDialog implements OnCompleteListener<AuthResult> {

    private static final String TAG = LogInDialog.class.getSimpleName();

    private FirebaseAuth mAuth;
    private LogInDialog.LogInListener mListener;

    public LogInDialog(Context context, FirebaseAuth pFirebaseAuth, LogInDialog.LogInListener pListener) {
        super(context);
        this.mAuth = pFirebaseAuth;
        this.mListener = pListener;
    }

    @Override
    protected int layoutView() {
        return R.layout.form_login;
    }

    @Override
    protected String title() { return "Connexion"; }

    @Override
    protected void positiveClick() {
        setProgressing(true);
        hideError();
        try {
            mAuth.signInWithEmailAndPassword(getMail(), getPassword()).addOnCompleteListener(this);
        } catch (Exception e){
            printError(e.getMessage());
            setProgressing(false);
        }
    }

    @Override
    protected void negativeClick() {
        dismiss();
    }

    @Override
    protected void neutralClick() {
        Toast.makeText(getContext(), "Neutral", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.et_login_mail).setOnFocusChangeListener(mFocusListener);
        findViewById(R.id.et_login_password).setOnFocusChangeListener(mFocusListener);

        String mail = PreferencesUtils.getStringPreferences(getContext(), getContext().getString(R.string.pref_user_mail));
        ((EditText) findViewById(R.id.et_login_mail)).setText(TextUtils.isEmpty(mail) ? "" : mail);
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        setProgressing(false);

        if (task.isSuccessful()) {
            if (mListener != null) { mListener.onUserConnected(mAuth.getCurrentUser()); }
            PreferencesUtils.setStringPreferences(getContext(), getContext().getString(R.string.pref_user_mail), getMail());
            dismiss();
        } else {
            Log.e(TAG, "signInWithEmail:failure", task.getException());
            printError(task.getException().getMessage());
        }
    }

    private String getMail(){
        EditText et = findViewById(R.id.et_login_mail);
        return TextUtils.isEmpty(et.getText()) ? "" : et.getText().toString();
    }

    private String getPassword(){
        EditText et = findViewById(R.id.et_login_password);
        return TextUtils.isEmpty(et.getText()) ? "" : et.getText().toString();
    }

    public interface LogInListener {
        void onUserConnected(FirebaseUser pUser);
    }
}
