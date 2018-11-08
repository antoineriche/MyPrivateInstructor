package com.antoineriche.privateinstructor.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.antoineriche.privateinstructor.R;

public class PreferencesUtils {

    public static SharedPreferences getDefaultSharedPreferences(Context pContext){
        return pContext.getSharedPreferences(pContext.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
    }

    public static boolean getBooleanPreferences(Context pContext, String pKey){
        return getBooleanPreferences(pContext, pKey, false);
    }

    public static boolean getBooleanPreferences(Context pContext, String pKey, boolean pDefaultValue){
        return getDefaultSharedPreferences(pContext).getBoolean(pKey, pDefaultValue);
    }

    public static void setLongPreferences(Context pContext, String pKey, long pValue){
        getDefaultSharedPreferences(pContext).edit().putLong(pKey, pValue).apply();
    }

    public static Long getLongPreferences(Context pContext, String pKey){
        return getLongPreferences(pContext, pKey, 0);
    }

    public static Long getLongPreferences(Context pContext, String pKey, long pDefaultValue){
        return getDefaultSharedPreferences(pContext).getLong(pKey, pDefaultValue);
    }

    public static void setStringPreferences(Context pContext, String pKey, String pValue){
        getDefaultSharedPreferences(pContext).edit().putString(pKey, pValue).apply();
    }

    public static String getStringPreferences(Context pContext, String pKey, String pDefaultValue){
        return getDefaultSharedPreferences(pContext).getString(pKey, pDefaultValue);
    }

    public static String getStringPreferences(Context pContext, String pKey){
        return getStringPreferences(pContext, pKey, "");
    }

}
