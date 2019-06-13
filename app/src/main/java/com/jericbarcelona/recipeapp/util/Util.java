package com.jericbarcelona.recipeapp.util;

import android.app.Activity;
import android.content.Context;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
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

            for(int i = 0; i < jsonArrayRecipe.length(); i++) {
                JSONObject jsonRecipeTypeItem = jsonArrayRecipe.getJSONObject(i);

                String recipeTypeUuid = jsonRecipeTypeItem.getString("uuid");
                String recipeTypeName = jsonRecipeTypeItem.getString("name");

                List<RecipeType> recipeTypes = daoSession.getRecipeTypeDao().queryBuilder().where(RecipeTypeDao.Properties.Uuid.eq(recipeTypeUuid)).list();
                if(recipeTypes.isEmpty()) {
                    RecipeType recipeType = new RecipeType();
                    recipeType.setUuid(recipeTypeUuid);
                    recipeType.setType(recipeTypeName);

                    recipeType.setCreatedAt(new Date());
                    recipeType.setUpdatedAt(new Date());

                    daoSession.getRecipeTypeDao().insert(recipeType);
                } else {
                    RecipeType recipeType = recipeTypes.get(0);
                    recipeType.setType(recipeTypeName);
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

                            recipeStepsItem.setCreatedAt(new Date());
                            recipeStepsItem.setUpdatedAt(new Date());

                            daoSession.getRecipeStepsDao().insert(recipeStepsItem);
                        } else {
                            RecipeSteps recipeStepsItem = recipeSteps.get(0);

                            recipeStepsItem.setValue(stepsValue);
                            recipeStepsItem.setNumber(stepsNumber);

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
}
