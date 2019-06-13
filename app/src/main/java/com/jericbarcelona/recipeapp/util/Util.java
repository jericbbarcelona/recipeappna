package com.jericbarcelona.recipeapp.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.jericbarcelona.recipeapp.App;
import com.jericbarcelona.recipeapp.helper.SharedPreferenceHelper;
import com.jericbarcelona.recipeapp.model.DaoSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class Util {

    public static SharedPreferenceHelper sp;

    public static void initializeSharedPreference(Context context) {
        if (sp == null) {
            sp = new SharedPreferenceHelper(context);
        }
    }

    public static String loadJSONFromAssets(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static String getLocalUuid() {
        UUID uuid = UUID.randomUUID();
        String randomUuid = uuid.toString();
        return randomUuid;
    }

    public static DaoSession getDaoSession(Activity activity) {
        DaoSession daoSession = ((App) activity.getApplication()).getDaoSession();
        return daoSession;
    }


    public static void loadRecipeDataFromJson(Activity activity) {
        try {
            DaoSession daoSession = Util.getDaoSession(activity);
            InputStream is = activity.getAssets().open("recipe_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            JSONObject jsonObject = new JSONObject(json);

            JSONArray jsonArrayRecipe = jsonObject.getJSONArray("data");

            Log.e("----jsonArrayRecipe----", jsonArrayRecipe.toString());

            for(int i = 0; i < jsonArrayRecipe.length(); i++) {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
