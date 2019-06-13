package com.jericbarcelona.recipeapp.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;

import com.jericbarcelona.recipeapp.App;
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
import java.io.IOException;
import java.io.InputStream;
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
                } else {
                    RecipeType recipeType = recipeTypes.get(0);
                    recipeType.setType(recipeTypeName);
                    recipeType.setImageLocation(recipeTypeImageLocation);
                    recipeType.setUpdatedAt(new Date());

                    daoSession.getRecipeTypeDao().update(recipeType);
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
                    } else {
                        RecipeDetails recipeDetailsItem = recipeDetails.get(0);

                        recipeDetailsItem.setName(recipeDetailsName);
                        recipeDetailsItem.setTypeUuid(recipeTypeUuid);
                        recipeDetailsItem.setCountry(recipeDetailsCountry);
                        recipeDetailsItem.setImageLocation(recipeDetailsImageLocation);
                        recipeDetailsItem.setDescription(recipeDetailsDescription);

                        recipeDetailsItem.setUpdatedAt(new Date());

                        daoSession.getRecipeDetailsDao().update(recipeDetailsItem);
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
                        } else {
                            RecipeIngredients recipeIngredientsItem = recipeIngredients.get(0);
                            recipeIngredientsItem.setValue(ingredientsValue);
                            recipeIngredientsItem.setDetailsUuid(recipeDetailsUuid);

                            recipeIngredientsItem.setUpdatedAt(new Date());

                            daoSession.getRecipeIngredientsDao().update(recipeIngredientsItem);
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
                        } else {
                            RecipeSteps recipeStepsItem = recipeSteps.get(0);

                            recipeStepsItem.setValue(stepsValue);
                            recipeStepsItem.setNumber(stepsNumber);
                            recipeStepsItem.setDetailsUuid(recipeDetailsUuid);

                            recipeStepsItem.setUpdatedAt(new Date());

                            daoSession.getRecipeStepsDao().update(recipeStepsItem);
                        }
                    }
                }
            }
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
}
