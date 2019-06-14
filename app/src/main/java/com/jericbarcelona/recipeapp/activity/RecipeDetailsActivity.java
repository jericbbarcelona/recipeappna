package com.jericbarcelona.recipeapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jericbarcelona.recipeapp.AppConstants;
import com.jericbarcelona.recipeapp.MainActivity;
import com.jericbarcelona.recipeapp.R;
import com.jericbarcelona.recipeapp.model.DaoSession;
import com.jericbarcelona.recipeapp.model.RecipeDetails;
import com.jericbarcelona.recipeapp.model.RecipeDetailsDao;
import com.jericbarcelona.recipeapp.model.RecipeIngredients;
import com.jericbarcelona.recipeapp.model.RecipeSteps;
import com.jericbarcelona.recipeapp.util.Util;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
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
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeView();

        Bundle extras = getIntent().getExtras();
        typeUuid = extras.getString(AppConstants.BUNDLE_TYPE_UUID);
        recipeType = extras.getString(AppConstants.BUNDLE_RECIPE_TYPE);

        getSupportActionBar().setTitle(recipeType + " Recipes");

        final DaoSession daoSession = Util.getDaoSession(this);
        final List<RecipeDetails> recipeDetailsList = daoSession.getRecipeDetailsDao().queryBuilder().where(RecipeDetailsDao.Properties.TypeUuid.eq(typeUuid)).list();
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

                final List<String> ingredientStrList = new ArrayList<>();
                final List<String> stepStrList = new ArrayList<>();

                final ImageView imageViewRecipe = dialogBuilder.findViewById(R.id.imageViewRecipe);
                final EditText editTextAddIngredients = dialogBuilder.findViewById(R.id.editTextAddIngredients);
                final EditText editTextAddStep = dialogBuilder.findViewById(R.id.editTextAddStep);
                final LinearLayout linearLayoutIngredients = dialogBuilder.findViewById(R.id.linearLayoutIngredients);
                final LinearLayout linearLayoutSteps = dialogBuilder.findViewById(R.id.linearLayoutSteps);
                final EditText editTextRecipeName = dialogBuilder.findViewById(R.id.editTextRecipeName);
                final EditText editTextDescription = dialogBuilder.findViewById(R.id.editTextDescription);

                FloatingActionButton floatingActionButtonAddRecipe = dialogBuilder.findViewById(R.id.floatingButtonAddRecipeDetails);
                Button buttonAddIngredients = dialogBuilder.findViewById(R.id.buttonAddIngredients);
                Button buttonAddStep = dialogBuilder.findViewById(R.id.buttonAddStep);

                ImageView imageViewClose = dialogBuilder.findViewById(R.id.imageButtonClose);

                floatingActionButtonAddRecipe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String recipeName = editTextRecipeName.getText().toString();
                        String recipeDescription = editTextDescription.getText().toString();

                        if(!recipeType.equals("") && !recipeName.equals("") && !recipeDescription.equals("")) {
                            RecipeDetails recipeDetails = new RecipeDetails();
                            recipeDetails.setUuid(Util.getLocalUuid());
                            recipeDetails.setTypeUuid(typeUuid);
                            recipeDetails.setName(recipeName);
                            recipeDetails.setDescription(recipeDescription);
                            daoSession.getRecipeDetailsDao().insert(recipeDetails);

                            for(String item : ingredientStrList) {
                                RecipeIngredients recipeIngredients = new RecipeIngredients();
                                recipeIngredients.setUuid(Util.getLocalUuid());
                                recipeIngredients.setDetailsUuid(recipeDetails.getUuid());
                                recipeIngredients.setValue(item);
                                recipeIngredients.setCreatedAt(new Date());
                                recipeIngredients.setUpdatedAt(new Date());

                                daoSession.getRecipeIngredientsDao().insert(recipeIngredients);
                            }

                            int counter = 1;
                            for(String item : stepStrList) {
                                RecipeSteps recipeSteps = new RecipeSteps();
                                recipeSteps.setUuid(Util.getLocalUuid());
                                recipeSteps.setDetailsUuid(recipeDetails.getUuid());
                                recipeSteps.setNumber(counter);
                                recipeSteps.setValue(item);
                                recipeSteps.setCreatedAt(new Date());
                                recipeSteps.setUpdatedAt(new Date());
                                counter++;
                                daoSession.getRecipeStepsDao().insert(recipeSteps);
                            }

                            Toast.makeText(RecipeDetailsActivity.this, "New recipe successfully added!", Toast.LENGTH_LONG).show();

                            dialogBuilder.dismiss();

                            final List<RecipeDetails> recipeDetailsList = daoSession.getRecipeDetailsDao().queryBuilder().list(); //daoSession.getRecipeDetailsDao().queryBuilder().where(RecipeDetailsDao.Properties.TypeUuid.eq(typeUuid)).list();
                            if (!recipeDetailsList.isEmpty()) {
                                initRecipeDetailsByType(linearLayoutRecipeDetails, recipeDetailsList);
                            }
                        } else {
                            Toast.makeText(RecipeDetailsActivity.this, "Please fill out the missing field(s).", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                buttonAddStep.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String step = editTextAddStep.getText().toString();
                        if(!step.equals("")) {
                            stepStrList.add(step);

                            initAddStep(linearLayoutSteps, stepStrList);

                            editTextAddStep.setText("");
                        } else {
                            Toast.makeText(RecipeDetailsActivity.this, "Please fill out the step field.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                buttonAddIngredients.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String ingredient = editTextAddIngredients.getText().toString();

                        if(!ingredient.equals("")) {
                            ingredientStrList.add(ingredient);

                            initAddIngredients(linearLayoutIngredients, ingredientStrList);

                            editTextAddIngredients.setText("");
                        } else {
                            Toast.makeText(RecipeDetailsActivity.this, "Please fill out the ingredient field.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

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
                        Window window = dialogBuilder.getWindow();
                        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
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

    private void initAddStep(LinearLayout linearLayout, List<String> stepStrList) {
        linearLayout.removeAllViews();

        int counter = 1;

        for(String item : stepStrList) {
            LinearLayout linearLayoutItem = (LinearLayout) getLayoutInflater().inflate(R.layout.instruction_item, null);
            TextView textView = linearLayoutItem.findViewById(R.id.textViewValue);

            textView.setText("* " + item);

            counter++;

            linearLayout.addView(linearLayoutItem);
        }
    }

    private void initAddIngredients(LinearLayout linearLayout, List<String> recipeIngredients) {
        linearLayout.removeAllViews();

        for(String item : recipeIngredients) {
            LinearLayout linearLayoutItem = (LinearLayout) getLayoutInflater().inflate(R.layout.ingredient_item, null);
            TextView textView = linearLayoutItem.findViewById(R.id.textViewValue);

            textView.setText(item);

            linearLayout.addView(linearLayoutItem);
        }
    }

    private void initRecipeDetailsByType(final LinearLayout linearLayout, List<RecipeDetails> recipeDetails) {
        final DaoSession daoSession = Util.getDaoSession(this);
        linearLayout.removeAllViews();

        for (final RecipeDetails recipeDetailsItem : recipeDetails) {
            LinearLayout linearLayoutItem = (LinearLayout) getLayoutInflater().inflate(R.layout.recipe_details_item, null);

            ImageButton imageViewTypeImage = linearLayoutItem.findViewById(R.id.imageViewTypeImage);
            TextView textViewTypeName = linearLayoutItem.findViewById(R.id.textViewName);
            TextView textViewDescription = linearLayoutItem.findViewById(R.id.textViewDescription);
            Button buttonDelete = linearLayoutItem.findViewById(R.id.buttonDelete);
            Button buttonView = linearLayoutItem.findViewById(R.id.buttonView);

            textViewTypeName.setText(recipeDetailsItem.getName() == null ? "" : recipeDetailsItem.getName());
            textViewDescription.setText(recipeDetailsItem.getDescription() == null ? "" : recipeDetailsItem.getDescription());

            File recipeTypeImageFile = new File(AppConstants.EXTERNAL_STORAGE, recipeDetailsItem.getImageLocation());
            if (recipeTypeImageFile.exists()) {
                String imageUri = "file://" + recipeTypeImageFile.getAbsolutePath();
                Util.imageLoader.displayImage(imageUri, imageViewTypeImage);
            } else {
                Bitmap placeholder = BitmapFactory.decodeResource(getResources(), R.drawable.recipe_logo_sample);
                imageViewTypeImage.setImageBitmap(placeholder);
            }


            buttonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(RecipeDetailsActivity.this, RecipeChosenDetailsActivity.class);
                    Bundle routeInfoBundle = new Bundle();
                    routeInfoBundle.putString(AppConstants.BUNDLE_DETAILS_UUID, recipeDetailsItem.getUuid());
                    routeInfoBundle.putString(AppConstants.BUNDLE_DETAILS_NAME, recipeDetailsItem.getName());
                    routeInfoBundle.putString(AppConstants.BUNDLE_TYPE_UUID, typeUuid);
                    routeInfoBundle.putString(AppConstants.BUNDLE_RECIPE_TYPE, recipeType);
                    intent.putExtras(routeInfoBundle);
                    startActivity(intent);
                }
            });

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(RecipeDetailsActivity.this);
                    alertDialog.setMessage("Are you sure you want to delete this item?");
                    alertDialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(RecipeDetailsActivity.this, recipeDetailsItem.getName() + " successfully deleted.", Toast.LENGTH_LONG).show();
                            daoSession.getRecipeDetailsDao().delete(recipeDetailsItem);
                            final List<RecipeDetails> recipeDetailsList = daoSession.getRecipeDetailsDao().queryBuilder().list();
                            initRecipeDetailsByType(linearLayout, recipeDetailsList);
                        }
                    });

                    alertDialog.show();
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
