package com.jericbarcelona.recipeapp;

import android.os.Environment;

public class AppConstants {

    public static final String EXTERNAL_STORAGE = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String RECIPE_TYPES_STORAGE = "/recipe_app/types/";
    public static final String CHICKEN_RECIPE_STORAGE = "/recipe_app/types/chicken/";
    public static final String BEEF_RECIPE_STORAGE = "/recipe_app/types/beef/";
    public static final String FISH_RECIPE_STORAGE = "/recipe_app/types/fish/";

    /*
        Database Stuff
     */
    public static final String DB_ENCRYPTED = "recipe_app_encrypted";
    public static final String DB_NOT_ENCRYPTED = "recipe_app";
    public static final String DB_PASSWORD = "recipeapp1234";

    /*
        Action Bar Strings
     */
    public static final String RECIPE_TYPES_LABEL = "Recipe Types";


    /*
        Recipe Type Strings
     */
    public static final String CHICKEN_TYPE = "Chicken";
    public static final String BEEF_TYPE = "Beef";
    public static final String FISH_TYPE = "Fish";

    /*
        Bundle Keys
     */
    public static final String BUNDLE_TYPE_UUID = "type_uuid";
    public static final String BUNDLE_RECIPE_TYPE = "recipe_type";
    public static final String BUNDLE_DETAILS_UUID = "details_uuid";
    public static final String BUNDLE_DETAILS_NAME = "details_name";

    /*
        SharedPref
     */
    public static final String SP_RECIPE_DATA_LOADED = "recipe_data_loaded";

}