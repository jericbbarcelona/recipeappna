package com.jericbarcelona.recipeapp.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;

public class SharedPreferenceHelper {
    private final Context mContext;

    public SharedPreferenceHelper(Context context) {
        mContext = context;
    }

    public static final String SHAREDPREFSIDENTIFIER = "recipe_app";

    public void showAllSharedPref() {
        SharedPreferences prefs = mContext.getSharedPreferences(SHAREDPREFSIDENTIFIER, mContext.MODE_PRIVATE);
        Map<String, ?> keys = prefs.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.e("map values", entry.getKey() + ": " +
                    entry.getValue().toString());
        }
    }

    public void setSharedPref(String identifier, String value) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(SHAREDPREFSIDENTIFIER, mContext.MODE_PRIVATE).edit();
        editor.putString(identifier, value);
        editor.commit();
    }

    public String getSharedPref(String identifier) {
        SharedPreferences prefs = mContext.getSharedPreferences(SHAREDPREFSIDENTIFIER, mContext.MODE_PRIVATE);
        String restoredText = prefs.getString(identifier, null);
        if (restoredText != null) {
            return restoredText;
        } else return null;
    }

    public void removeSharedPref(String identifier) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(SHAREDPREFSIDENTIFIER, mContext.MODE_PRIVATE).edit();
        editor.remove(identifier);
        editor.apply();
    }
}