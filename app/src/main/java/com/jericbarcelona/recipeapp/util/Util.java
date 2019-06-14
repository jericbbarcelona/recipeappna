package com.jericbarcelona.recipeapp.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.inputmethod.InputMethodManager;

import com.jericbarcelona.recipeapp.App;
import com.jericbarcelona.recipeapp.AppConstants;
import com.jericbarcelona.recipeapp.helper.SharedPreferenceHelper;
import com.jericbarcelona.recipeapp.model.DaoSession;
import com.jericbarcelona.recipeapp.model.RecipeDetails;
import com.jericbarcelona.recipeapp.model.RecipeDetailsDao;
import com.jericbarcelona.recipeapp.model.RecipeIngredients;
import com.jericbarcelona.recipeapp.model.RecipeIngredientsDao;
import com.jericbarcelona.recipeapp.model.RecipeSteps;
import com.jericbarcelona.recipeapp.model.RecipeStepsDao;
import com.jericbarcelona.recipeapp.model.RecipeType;
import com.jericbarcelona.recipeapp.model.RecipeTypeDao;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Util {

    public static SharedPreferenceHelper sp;
    public static ImageLoader imageLoader;

    public static void initializeSharedPreference(Context context) {
        if (sp == null) {
            sp = new SharedPreferenceHelper(context);
        }
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

            for(int i = 0; i < jsonArrayRecipe.length(); i++) {
                JSONObject jsonRecipeTypeItem = jsonArrayRecipe.getJSONObject(i);

                String recipeTypeUuid = jsonRecipeTypeItem.getString("uuid");
                String recipeTypeName = jsonRecipeTypeItem.getString("name");
                String recipeTypeImageLocation = jsonRecipeTypeItem.getString("image_location");

                List<RecipeType> recipeTypes = daoSession.getRecipeTypeDao().queryBuilder().where(RecipeTypeDao.Properties.Uuid.eq(recipeTypeUuid)).list();
                if(recipeTypes.isEmpty()) {
                    RecipeType recipeType = new RecipeType();
                    recipeType.setUuid(recipeTypeUuid);
                    recipeType.setType(recipeTypeName);
                    recipeType.setImageLocation(recipeTypeImageLocation);

                    recipeType.setCreatedAt(new Date());
                    recipeType.setUpdatedAt(new Date());

                    daoSession.getRecipeTypeDao().insert(recipeType);
                }

                JSONArray jsonArrayRecipeDetails = jsonRecipeTypeItem.getJSONArray("details");
                for(int j = 0; j < jsonArrayRecipeDetails.length(); j++) {
                    JSONObject jsonObjectRecipeDetailsItem = jsonArrayRecipeDetails.getJSONObject(j);

                    String recipeDetailsUuid = jsonObjectRecipeDetailsItem.getString("uuid");
                    String recipeDetailsName = jsonObjectRecipeDetailsItem.getString("name");
                    String recipeDetailsDescription = jsonObjectRecipeDetailsItem.getString("description");
                    String recipeDetailsCountry = jsonObjectRecipeDetailsItem.getString("country");
                    String recipeDetailsImageLocation = jsonObjectRecipeDetailsItem.getString("image_location");

                    List<RecipeDetails> recipeDetails = daoSession.getRecipeDetailsDao().queryBuilder().where(RecipeDetailsDao.Properties.Uuid.eq(recipeDetailsUuid)).list();
                    if(recipeDetails.isEmpty()) {
                        RecipeDetails recipeDetailsItem = new RecipeDetails();
                        recipeDetailsItem.setUuid(recipeDetailsUuid);
                        recipeDetailsItem.setTypeUuid(recipeTypeUuid);
                        recipeDetailsItem.setName(recipeDetailsName);
                        recipeDetailsItem.setCountry(recipeDetailsCountry);
                        recipeDetailsItem.setImageLocation(recipeDetailsImageLocation);
                        recipeDetailsItem.setDescription(recipeDetailsDescription);

                        recipeDetailsItem.setCreatedAt(new Date());
                        recipeDetailsItem.setUpdatedAt(new Date());

                        daoSession.getRecipeDetailsDao().insert(recipeDetailsItem);
                    }

                    JSONArray jsonArrayIngredients = jsonObjectRecipeDetailsItem.getJSONArray("ingredients");
                    for(int k = 0; k < jsonArrayIngredients.length(); k++) {
                        JSONObject jsonObjectIngredients = jsonArrayIngredients.getJSONObject(k);

                        String ingredientsUuid = jsonObjectIngredients.getString("uuid");
                        String ingredientsValue = jsonObjectIngredients.getString("value");

                        List<RecipeIngredients> recipeIngredients = daoSession.getRecipeIngredientsDao().queryBuilder().where(RecipeIngredientsDao.Properties.Uuid.eq(ingredientsUuid)).list();
                        if(recipeIngredients.isEmpty()) {
                            RecipeIngredients recipeIngredientsItem = new RecipeIngredients();
                            recipeIngredientsItem.setUuid(ingredientsUuid);
                            recipeIngredientsItem.setValue(ingredientsValue);
                            recipeIngredientsItem.setDetailsUuid(recipeDetailsUuid);

                            recipeIngredientsItem.setCreatedAt(new Date());
                            recipeIngredientsItem.setUpdatedAt(new Date());

                            daoSession.getRecipeIngredientsDao().insert(recipeIngredientsItem);
                        }
                    }

                    JSONArray jsonArraySteps = jsonObjectRecipeDetailsItem.getJSONArray("steps");
                    for(int l = 0; l < jsonArraySteps.length(); l++) {
                        JSONObject jsonObjectStepsItem = jsonArraySteps.getJSONObject(l);

                        String stepsUuid = jsonObjectStepsItem.getString("uuid");
                        String stepsValue = jsonObjectStepsItem.getString("value");
                        int stepsNumber = jsonObjectStepsItem.getInt("number");

                        List<RecipeSteps> recipeSteps = daoSession.getRecipeStepsDao().queryBuilder().where(RecipeStepsDao.Properties.Uuid.eq(stepsUuid)).list();
                        if(recipeSteps.isEmpty()) {
                            RecipeSteps recipeStepsItem = new RecipeSteps();
                            recipeStepsItem.setUuid(stepsUuid);
                            recipeStepsItem.setValue(stepsValue);
                            recipeStepsItem.setNumber(stepsNumber);
                            recipeStepsItem.setDetailsUuid(recipeDetailsUuid);

                            recipeStepsItem.setCreatedAt(new Date());
                            recipeStepsItem.setUpdatedAt(new Date());

                            daoSession.getRecipeStepsDao().insert(recipeStepsItem);
                        }
                    }
                }
            }

            Util.sp.setSharedPref(AppConstants.SP_RECIPE_DATA_LOADED, "IS_LOADED");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Intent getPickImageChooserIntent(Context context) {
        Uri outputFileUri = getCaptureImageOutputUri(context);
        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();

        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));
        return chooserIntent;
    }

    public static Uri getCaptureImageOutputUri(Context context) {
        Uri outputFileUri = null;
        File getImage = context.getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    public static void copyAssets(Context context) {

        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (files != null) {
            for (String filename : files) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open(filename);
                    File storage = new File(AppConstants.EXTERNAL_STORAGE + AppConstants.RECIPE_TYPES_STORAGE);
                    if (!storage.exists()) {
                        storage.mkdirs();
                    }
                    File chickenStorage = new File(AppConstants.EXTERNAL_STORAGE + AppConstants.CHICKEN_RECIPE_STORAGE);
                    File beefStorage = new File(AppConstants.EXTERNAL_STORAGE + AppConstants.BEEF_RECIPE_STORAGE);
                    File fishStorage = new File(AppConstants.EXTERNAL_STORAGE + AppConstants.FISH_RECIPE_STORAGE);

                    if(!chickenStorage.exists()) {
                        chickenStorage.mkdirs();
                    }
                    if(!beefStorage.exists()) {
                        beefStorage.mkdirs();
                    }
                    if(!fishStorage.exists()) {
                        fishStorage.mkdirs();
                    }

                    if(filename.equals("chicken_curry.png")) {
                        File outFile = new File(AppConstants.EXTERNAL_STORAGE + AppConstants.CHICKEN_RECIPE_STORAGE, filename);
                        out = new FileOutputStream(outFile);
                        copyFile(in, out);
                    } else if(filename.equals("beef_steak.jpg")) {
                        File outFile = new File(AppConstants.EXTERNAL_STORAGE + AppConstants.BEEF_RECIPE_STORAGE, filename);
                        out = new FileOutputStream(outFile);
                        copyFile(in, out);
                    } else if(filename.equals("fish_fillet.jpg")) {
                        File outFile = new File(AppConstants.EXTERNAL_STORAGE + AppConstants.FISH_RECIPE_STORAGE, filename);
                        out = new FileOutputStream(outFile);
                        copyFile(in, out);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws Exception {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
