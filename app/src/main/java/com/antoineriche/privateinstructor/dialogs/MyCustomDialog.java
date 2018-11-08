package com.antoineriche.privateinstructor.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.antoineriche.privateinstructor.R;
import com.antoineriche.privateinstructor.utils.PreferencesUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public abstract class MyCustomDialog extends AlertDialog {

    View.OnFocusChangeListener mFocusListener = (view, hasFocus) -> {
        if (hasFocus) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    |WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        }
    };

    MyCustomDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutInflater().inflate(R.layout.default_dialog, null));

        LinearLayout ll = findViewById(R.id.ll_dialog_content);
        ll.addView(getLayoutInflater().inflate(layoutView(), null));

        ((TextView) findViewById(R.id.tv_dialog_title)).setText(title());

        findViewById(R.id.btn_positive).setOnClickListener(v -> positiveClick());
        findViewById(R.id.btn_negative).setOnClickListener(v -> negativeClick());

        hideProgress();
        hideError();
    }

    private void hideProgress(){
        setProgressVisible(false);
    }

    public void showProgress(){
        setProgressVisible(true);
    }

    private void setProgressVisible(boolean visible){
        findViewById(R.id.dialog_progress).setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    void hideError(){
        setError(null);
    }

    void printError(String pError){
        setError(pError);
    }

    private void setError(String pError){
        boolean visible = !TextUtils.isEmpty(pError);
        TextView tvError = findViewById(R.id.tv_dialog_error);
        tvError.setVisibility(visible ? View.VISIBLE : View.GONE);
        tvError.setText(pError);
    }

    void setProgressing(boolean isProgressing){
        findViewById(R.id.btn_positive).setEnabled(!isProgressing);
        findViewById(R.id.btn_negative).setEnabled(!isProgressing);
        setProgressVisible(isProgressing);
    }

    protected abstract int layoutView();

    protected abstract String title();

    protected abstract void positiveClick();

    protected abstract void negativeClick();

    protected abstract void neutralClick();

}
