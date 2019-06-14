package com.jericbarcelona.recipeapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        getSupportActionBar().setTitle("Recipe Types");

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

        initializeView();

        Util.loadRecipeDataFromJson(this);

        initRecipeTypes(linearLayoutRecipeTypes);
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

            if(recipeTypeItem.getType().equals("Chicken")) {
                Bitmap placeholder = BitmapFactory.decodeResource(getResources(), R.drawable.chicken);
                imageViewTypeImage.setImageBitmap(placeholder);
            } else if(recipeTypeItem.getType().equals("Fish")) {
                Bitmap placeholder = BitmapFactory.decodeResource(getResources(), R.drawable.fish);
                imageViewTypeImage.setImageBitmap(placeholder);
            } else if(recipeTypeItem.getType().equals("Beef")) {
                Bitmap placeholder = BitmapFactory.decodeResource(getResources(), R.drawable.beef);
                imageViewTypeImage.setImageBitmap(placeholder);
            }

//            File recipeTypeImageFile = new File(AppConstants.EXTERNAL_STORAGE, recipeTypeItem.getImageLocation());
//            if (recipeTypeImageFile.exists()) {
//                String imageUri = "file://" + recipeTypeImageFile.getAbsolutePath();
//                Util.imageLoader.displayImage(imageUri, imageViewTypeImage);
//            }

            buttonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, RecipeDetailsActivity.class);
                    Bundle routeInfoBundle = new Bundle();
                    routeInfoBundle.putString("type_uuid", recipeTypeItem.getUuid());
                    routeInfoBundle.putString("recipe_type", recipeTypeItem.getType());
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
