package com.jericbarcelona.recipeapp.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jericbarcelona.recipeapp.MainActivity;
import com.jericbarcelona.recipeapp.R;
import com.jericbarcelona.recipeapp.model.DaoSession;
import com.jericbarcelona.recipeapp.model.RecipeDetails;
import com.jericbarcelona.recipeapp.util.Util;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;

public class RecipeDetailsActivity extends AppCompatActivity {

    private LinearLayout linearLayoutRecipeDetails;
    private FloatingActionButton floatingButtonAddRecipeDetails;
    private CropImageView imageViewRecipeCrop;
    private Bitmap cropped;
    private String typeUuid = "";
    private String recipeType = "";

    private void initializeView() {
        linearLayoutRecipeDetails = (LinearLayout) findViewById(R.id.linearLayoutRecipeDetails);
        floatingButtonAddRecipeDetails = (FloatingActionButton) findViewById(R.id.floatingButtonAddRecipeDetails);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(RecipeDetailsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeView();

        Bundle extras = getIntent().getExtras();
        typeUuid = extras.getString("type_uuid");
        recipeType = extras.getString("recipe_type");

        getSupportActionBar().setTitle(recipeType + " Recipes");

        DaoSession daoSession = Util.getDaoSession(this);
        List<RecipeDetails> recipeDetailsList = daoSession.getRecipeDetailsDao().queryBuilder().list(); //daoSession.getRecipeDetailsDao().queryBuilder().where(RecipeDetailsDao.Properties.TypeUuid.eq(typeUuid)).list();
        if (!recipeDetailsList.isEmpty()) {
            initRecipeDetailsByType(linearLayoutRecipeDetails, recipeDetailsList);
        } else {
            Toast.makeText(this, "No " + recipeType + " recipe at this moment.", Toast.LENGTH_LONG).show();
        }

        floatingButtonAddRecipeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialogBuilder = new Dialog(RecipeDetailsActivity.this);
                dialogBuilder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogBuilder.setContentView(R.layout.add_update_recipe_details_dialog);
                Window window = dialogBuilder.getWindow();
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                dialogBuilder.setCancelable(false);
                dialogBuilder.show();

                final ImageView imageViewRecipe = dialogBuilder.findViewById(R.id.imageViewRecipe);
                ImageView imageViewClose = dialogBuilder.findViewById(R.id.imageButtonClose);

                imageViewClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogBuilder.dismiss();
                    }
                });

                imageViewRecipe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivityForResult(Util.getPickImageChooserIntent(RecipeDetailsActivity.this), 200);

                        final Dialog recipeImageDialog = new Dialog(RecipeDetailsActivity.this);
                        recipeImageDialog.setContentView(R.layout.image_cropper);
                        recipeImageDialog.setTitle("Capture Recipe Image");
                        recipeImageDialog.setCancelable(true);
                        recipeImageDialog.show();
                        recipeImageDialog.getWindow().setTitleColor(getResources().getColor(R.color.colorAccent));

                        imageViewRecipeCrop = recipeImageDialog.findViewById(R.id.profilePictureCropImageView);
                        Button buttonCropImage = recipeImageDialog.findViewById(R.id.buttonCropImage);

                        buttonCropImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cropped = imageViewRecipeCrop.getCroppedImage(500, 500);
                                if (cropped != null) {
                                    imageViewRecipe.setImageBitmap(cropped);
                                    /*try {
                                        File photoDir = new File(AppConstants.PROFILE_PICTURE_EXTERNAL_STORAGE);
                                        if (!photoDir.exists()) {
                                            photoDir.mkdirs();
                                        }
                                    } catch (SecurityException se) {
                                        Log.e("Create Folder", se.toString());
                                    }
                                    String fileName = customerId + ".jpg";
                                    File file = new File(AppConstants.PROFILE_PICTURE_EXTERNAL_STORAGE, fileName);
                                    try {
                                        FileOutputStream out = new FileOutputStream(file);
                                        cropped.compress(Bitmap.CompressFormat.JPEG, 20, out);
                                        out.flush();
                                        out.close();

                                        List<WACustomer> waCustomerList = daoSession.getWACustomerDao().queryBuilder().where(WACustomerDao.Properties.CustomerId.eq(customerId)).list();
                                        if (!waCustomerList.isEmpty()) {
                                            WACustomer waCustomer = waCustomerList.get(0);
                                            waCustomer.setProfilePicPath(file.getName());
                                            daoSession.getWACustomerDao().update(waCustomer);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }*/
                                    recipeImageDialog.cancel();
                                }
                            }
                        });

                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri = getPickImageResultUri(data, RecipeDetailsActivity.this);
            imageViewRecipeCrop.setImageUriAsync(imageUri);
            imageViewRecipeCrop.setFixedAspectRatio(true);
            imageViewRecipeCrop.setAspectRatio(180, 180);
        }
    }

    public Uri getPickImageResultUri(Intent data, Context context) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? Util.getCaptureImageOutputUri(context) : data.getData();
    }

    private void initRecipeDetailsByType(LinearLayout linearLayout, List<RecipeDetails> recipeDetails) {
        linearLayout.removeAllViews();
        for (final RecipeDetails recipeDetailsItem : recipeDetails) {
            LinearLayout linearLayoutItem = (LinearLayout) getLayoutInflater().inflate(R.layout.recipe_details_item, null);

            ImageButton imageViewTypeImage = linearLayoutItem.findViewById(R.id.imageViewTypeImage);
            TextView textViewTypeName = linearLayoutItem.findViewById(R.id.textViewName);
            TextView textViewDescription = linearLayoutItem.findViewById(R.id.textViewDescription);

            textViewTypeName.setText(recipeDetailsItem.getName() == null ? "" : recipeDetailsItem.getName());
            textViewDescription.setText(recipeDetailsItem.getDescription() == null ? "" : recipeDetailsItem.getDescription());

            Bitmap placeholder = BitmapFactory.decodeResource(getResources(), R.drawable.beef);
            imageViewTypeImage.setImageBitmap(placeholder);

            imageViewTypeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(RecipeDetailsActivity.this, RecipeChosenDetailsActivity.class);
                    Bundle routeInfoBundle = new Bundle();
                    routeInfoBundle.putString("details_uuid", recipeDetailsItem.getUuid());
                    routeInfoBundle.putString("details_name", recipeDetailsItem.getName());
                    routeInfoBundle.putString("type_uuid", typeUuid);
                    routeInfoBundle.putString("recipe_type", recipeType);
                    intent.putExtras(routeInfoBundle);
                    startActivity(intent);
                }
            });

//            File recipeTypeImageFile = new File(AppConstants.EXTERNAL_STORAGE, recipeDetailsItem.getImageLocation());
//            if (recipeTypeImageFile.exists()) {
//                String imageUri = "file://" + recipeTypeImageFile.getAbsolutePath();
//                Util.imageLoader.displayImage(imageUri, imageViewTypeImage);
//            }

            linearLayout.addView(linearLayoutItem);
        }
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
