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

    /**
     * Display all values stored in shared preference
     */
    public void showAllSharedPref() {
        SharedPreferences prefs = mContext.getSharedPreferences(SHAREDPREFSIDENTIFIER, mContext.MODE_PRIVATE);
        Map<String, ?> keys = prefs.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.e("map values", entry.getKey() + ": " +
                    entry.getValue().toString());
        }
    }

    /**
     * Set a new entry in the SharedPreference
     *
     * @param identifier is the desired identifier/key for the preference value
     * @param value      is the value for the said identifier/key
     */
    public void setSharedPref(String identifier, String value) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(SHAREDPREFSIDENTIFIER, mContext.MODE_PRIVATE).edit();
        editor.putString(identifier, value);
        editor.commit();
    }

    /**
     * Get a value for the specific identifier/key
     *
     * @param identifier is the identifier/key that will be looked up.
     * @return returns the found value or null if not found
     */
    public String getSharedPref(String identifier) {
        SharedPreferences prefs = mContext.getSharedPreferences(SHAREDPREFSIDENTIFIER, mContext.MODE_PRIVATE);
        String restoredText = prefs.getString(identifier, null);
        if (restoredText != null) {
            return restoredText;
        } else return null;
    }

    /**
     * Removes the entry on the specified identifier/key
     *
     * @param identifier
     */
    public void removeSharedPref(String identifier) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(SHAREDPREFSIDENTIFIER, mContext.MODE_PRIVATE).edit();
        editor.remove(identifier);
        editor.apply();
    }

}