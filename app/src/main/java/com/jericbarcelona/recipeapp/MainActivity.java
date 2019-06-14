package com.jericbarcelona.recipeapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jericbarcelona.recipeapp.activity.RecipeDetailsActivity;
import com.jericbarcelona.recipeapp.model.DaoSession;
import com.jericbarcelona.recipeapp.model.RecipeType;
import com.jericbarcelona.recipeapp.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    LinearLayout linearLayoutRecipeTypes;

    private void initializeView() {
        linearLayoutRecipeTypes = (LinearLayout) findViewById(R.id.linearLayoutRecipeTypes);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(AppConstants.RECIPE_TYPES_LABEL);

        Util.initializeSharedPreference(getApplicationContext());

        if (Util.imageLoader == null) {
            Util.imageLoader = ImageLoader.getInstance();
            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .cacheInMemory(false)
                    .cacheOnDisk(false)
                    .build();

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                    .defaultDisplayImageOptions(defaultOptions)
                    .diskCacheExtraOptions(1000, 1000, null)
                    .build();

            Util.imageLoader.init(config);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                //do your task
            } else {
                requestPermission();
            }
        }

        Util.copyAssets(getApplicationContext());

        initializeView();

        boolean isRecipeDataLoaded = Util.sp.getSharedPref(AppConstants.SP_RECIPE_DATA_LOADED) != null ? true : false;
        if(!isRecipeDataLoaded) {
            Util.loadRecipeDataFromJson(this);
        }

        initRecipeTypes(linearLayoutRecipeTypes);
    }

    protected boolean checkPermission()
    {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);

        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Read External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Util.copyAssets(getApplicationContext());
                    //check here code is needed or not
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                    Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void initRecipeTypes(LinearLayout linearLayout) {
        linearLayout.removeAllViews();

        DaoSession daoSession = Util.getDaoSession(this);
        List<RecipeType> recipeTypes = daoSession.getRecipeTypeDao().queryBuilder().list();
        for(final RecipeType recipeTypeItem : recipeTypes) {
            LinearLayout linearLayoutItem = (LinearLayout) getLayoutInflater().inflate(R.layout.recipe_types_item, null);

            ImageButton imageViewTypeImage = linearLayoutItem.findViewById(R.id.imageViewTypeImage);
            TextView textViewTypeName = linearLayoutItem.findViewById(R.id.textViewTypeName);
            Button buttonView = linearLayoutItem.findViewById(R.id.buttonView);

            textViewTypeName.setText(recipeTypeItem.getType());

            if(recipeTypeItem.getType().equals(AppConstants.CHICKEN_TYPE)) {
                Bitmap placeholder = BitmapFactory.decodeResource(getResources(), R.drawable.chicken);
                imageViewTypeImage.setImageBitmap(placeholder);
            } else if(recipeTypeItem.getType().equals(AppConstants.FISH_TYPE)) {
                Bitmap placeholder = BitmapFactory.decodeResource(getResources(), R.drawable.fish);
                imageViewTypeImage.setImageBitmap(placeholder);
            } else if(recipeTypeItem.getType().equals(AppConstants.BEEF_TYPE)) {
                Bitmap placeholder = BitmapFactory.decodeResource(getResources(), R.drawable.beef);
                imageViewTypeImage.setImageBitmap(placeholder);
            }

            buttonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, RecipeDetailsActivity.class);
                    Bundle routeInfoBundle = new Bundle();
                    routeInfoBundle.putString(AppConstants.BUNDLE_TYPE_UUID, recipeTypeItem.getUuid());
                    routeInfoBundle.putString(AppConstants.BUNDLE_RECIPE_TYPE, recipeTypeItem.getType());
                    intent.putExtras(routeInfoBundle);
                    startActivity(intent);
                }
            });

            linearLayout.addView(linearLayoutItem);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
