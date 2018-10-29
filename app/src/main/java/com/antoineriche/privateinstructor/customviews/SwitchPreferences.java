package com.antoineriche.privateinstructor.customviews;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.antoineriche.privateinstructor.R;

public class SwitchPreferences extends RelativeLayout {

    private String label, description;
    private SwitchPreferencesListener mSwitchPreferencesListener;
    private String mPreferencesKey;
    private SharedPreferences mSharedPreferences;

    public SwitchPreferences(Context context) {
        this(context, null);
    }

    public SwitchPreferences(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchPreferences);
        label = typedArray.getString(R.styleable.SwitchPreferences_preferencesLabel);
        description = typedArray.getString(R.styleable.SwitchPreferences_preferencesDescription);
        mPreferencesKey = typedArray.getString(R.styleable.SwitchPreferences_preferences_key);
        typedArray.recycle();

        init();
    }

    private void init(){
        inflate(getContext(), R.layout.custom_view_switch_preferences, this);

        getTextViewLabel().setText(label);
        getTextViewDescription().setText(description);
        getSwitch().setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(mSwitchPreferencesListener != null){
                mSwitchPreferencesListener.isChecked(isChecked);
            }

            if(isSetUp()){
                mSharedPreferences.edit().putBoolean(mPreferencesKey, isChecked).apply();
            } else {
                if (mSwitchPreferencesListener != null){
                    Log.e("SwitchPreferences", "Please, set up this switch before");
                    mSwitchPreferencesListener.updateError("No SharedPreferences associated", mPreferencesKey);
                }
            }
        });

        if(isInEditMode()){
            getTextViewLabel().setText(!TextUtils.isEmpty(label) ? label : "Default preferences");
            getTextViewDescription().setText(!TextUtils.isEmpty(description) ? description : "Default preferences description");
            getSwitch().setChecked(true);
        }
    }

    private Switch getSwitch(){
        return findViewById(R.id.preferences_switch);
    }

    private TextView getTextViewLabel(){
        return findViewById(R.id.preferences_tv_label);
    }

    private TextView getTextViewDescription(){
        return findViewById(R.id.preferences_tv_description);
    }

    public void setPreferencesKey(String preferencesKey){
        mPreferencesKey = preferencesKey;
    }

    public void setSharedPreferences(SharedPreferences pSharedPreferences){
        mSharedPreferences = pSharedPreferences;
        getSwitch().setChecked(mSharedPreferences.getBoolean(mPreferencesKey, false));
        mSharedPreferences.registerOnSharedPreferenceChangeListener((sharedPreferences, pKey) -> {
            if(mSwitchPreferencesListener != null && pKey == mPreferencesKey){
                mSwitchPreferencesListener.preferencesUpdated(pKey, sharedPreferences.getBoolean(pKey, false));
            }
        });
    }

    public void setSwitchPreferencesListener(SwitchPreferencesListener pSwitchListener){
        mSwitchPreferencesListener = pSwitchListener;
    }

    public boolean isSetUp(){
        return mSharedPreferences != null && !TextUtils.isEmpty(mPreferencesKey);
    }

    public void setChecked(boolean checked){
        getSwitch().setChecked(checked);
    }

    public boolean isChecked(){
        return getSwitch().isChecked();
    }

    public interface SwitchPreferencesListener {
        void isChecked(boolean isChecked);
        void preferencesUpdated(String pKey, boolean pValue);
        void updateError(String pError, String pKey);
    }
}
