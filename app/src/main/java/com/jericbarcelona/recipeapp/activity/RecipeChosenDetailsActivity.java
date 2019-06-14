package com.jericbarcelona.recipeapp.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jericbarcelona.recipeapp.AppConstants;
import com.jericbarcelona.recipeapp.R;
import com.jericbarcelona.recipeapp.model.DaoSession;
import com.jericbarcelona.recipeapp.model.RecipeDetails;
import com.jericbarcelona.recipeapp.model.RecipeDetailsDao;
import com.jericbarcelona.recipeapp.model.RecipeIngredients;
import com.jericbarcelona.recipeapp.model.RecipeIngredientsDao;
import com.jericbarcelona.recipeapp.model.RecipeSteps;
import com.jericbarcelona.recipeapp.model.RecipeStepsDao;
import com.jericbarcelona.recipeapp.util.Util;

import java.io.File;
import java.util.Date;
import java.util.List;

public class RecipeChosenDetailsActivity extends AppCompatActivity {

    private LinearLayout linearLayoutIngredients;
    private LinearLayout linearLayoutSteps;
    private FloatingActionButton floatingButtonEditRecipeDetails;
    private TextView textViewName;
    private TextView textViewDescription;
    private ImageView imageViewRecipe;

    private String typeUuid = "";
    private String recipeType = "";

    private void initializeView() {
        linearLayoutIngredients = (LinearLayout) findViewById(R.id.linearLayoutIngredients);
        linearLayoutSteps = (LinearLayout) findViewById(R.id.linearLayoutSteps);
        floatingButtonEditRecipeDetails = (FloatingActionButton) findViewById(R.id.floatingButtonEditRecipeDetails);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewDescription = (TextView) findViewById(R.id.textViewDescription);
        imageViewRecipe = (ImageView) findViewById(R.id.imageViewRecipe);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(RecipeChosenDetailsActivity.this, RecipeDetailsActivity.class);
        Bundle routeInfoBundle = new Bundle();
        routeInfoBundle.putString(AppConstants.BUNDLE_TYPE_UUID, typeUuid);
        routeInfoBundle.putString(AppConstants.BUNDLE_RECIPE_TYPE, recipeType);
        intent.putExtras(routeInfoBundle);
        startActivity(intent);
        finish();
        return true;
    }

    private List<RecipeDetails> populateRecipeDetailsData(DaoSession daoSession, String detailsUuid) {
        final List<RecipeDetails> recipeDetails = daoSession.getRecipeDetailsDao().queryBuilder().where(RecipeDetailsDao.Properties.Uuid.eq(detailsUuid)).list();

        if (!recipeDetails.isEmpty()) {
            RecipeDetails recipeDetailsItem = recipeDetails.get(0);

            textViewName.setText(recipeDetailsItem.getName());
            textViewDescription.setText(recipeDetailsItem.getDescription());

            if(recipeDetailsItem.getImageLocation() != null && !recipeDetailsItem.getImageLocation().equals("")) {
                File recipeTypeImageFile = new File(AppConstants.EXTERNAL_STORAGE, recipeDetailsItem.getImageLocation());
                if (recipeTypeImageFile.exists()) {
                    String imageUri = "file://" + recipeTypeImageFile.getAbsolutePath();
                    Util.imageLoader.displayImage(imageUri, imageViewRecipe);
                } else {
                    Bitmap placeholder = BitmapFactory.decodeResource(getResources(), R.drawable.recipe_logo_sample);
                    imageViewRecipe.setImageBitmap(placeholder);
                }
            } else {
                Bitmap placeholder = BitmapFactory.decodeResource(getResources(), R.drawable.recipe_logo_sample);
                imageViewRecipe.setImageBitmap(placeholder);
            }

            List<RecipeIngredients> recipeIngredients = daoSession.getRecipeIngredientsDao().queryBuilder().where(RecipeIngredientsDao.Properties.DetailsUuid.eq(recipeDetailsItem.getUuid())).list();
            if (!recipeIngredients.isEmpty()) {
                initIngredients(linearLayoutIngredients, recipeIngredients);
            }

            List<RecipeSteps> recipeSteps = daoSession.getRecipeStepsDao().queryBuilder().where(RecipeStepsDao.Properties.DetailsUuid.eq(recipeDetailsItem.getUuid())).list();
            if (!recipeSteps.isEmpty()) {
                initSteps(linearLayoutSteps, recipeSteps);
            }
        }

        return recipeDetails;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_chosen_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeView();

        Bundle extras = getIntent().getExtras();

        final String detailsUuid = extras.getString(AppConstants.BUNDLE_DETAILS_UUID);
        String detailsName = extras.getString(AppConstants.BUNDLE_DETAILS_NAME);
        typeUuid = extras.getString(AppConstants.BUNDLE_TYPE_UUID);
        recipeType = extras.getString(AppConstants.BUNDLE_RECIPE_TYPE);

        getSupportActionBar().setTitle(detailsName + " Recipe");

        final DaoSession daoSession = Util.getDaoSession(this);

        final List<RecipeDetails> recipeDetails = populateRecipeDetailsData(daoSession, detailsUuid);

        floatingButtonEditRecipeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialogBuilder = new Dialog(RecipeChosenDetailsActivity.this);
                dialogBuilder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogBuilder.setContentView(R.layout.add_update_recipe_details_dialog);
                dialogBuilder.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                Window window = dialogBuilder.getWindow();
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                dialogBuilder.setCancelable(false);
                dialogBuilder.show();

                FloatingActionButton floatingActionButtonAddRecipe = dialogBuilder.findViewById(R.id.floatingButtonAddRecipeDetails);
                final ImageView imageViewRecipe = dialogBuilder.findViewById(R.id.imageViewRecipe);
                final EditText editTextAddIngredients = dialogBuilder.findViewById(R.id.editTextAddIngredients);
                final EditText editTextAddStep = dialogBuilder.findViewById(R.id.editTextAddStep);
                final LinearLayout linearLayoutIngredients = dialogBuilder.findViewById(R.id.linearLayoutIngredients);
                final LinearLayout linearLayoutSteps = dialogBuilder.findViewById(R.id.linearLayoutSteps);
                final EditText editTextRecipeName = dialogBuilder.findViewById(R.id.editTextRecipeName);
                final EditText editTextDescription = dialogBuilder.findViewById(R.id.editTextDescription);

                ImageView imageViewClose = dialogBuilder.findViewById(R.id.imageButtonClose);
                Button buttonAddIngredients = dialogBuilder.findViewById(R.id.buttonAddIngredients);
                Button buttonAddStep = dialogBuilder.findViewById(R.id.buttonAddStep);

                if (!recipeDetails.isEmpty()) {
                    RecipeDetails recipeDetailsItem = recipeDetails.get(0);
                    editTextRecipeName.setText(recipeDetailsItem.getName());
                    editTextDescription.setText(recipeDetailsItem.getDescription());

                    if(recipeDetailsItem.getImageLocation() != null && !recipeDetailsItem.getImageLocation().equals("")) {
                        File recipeTypeImageFile = new File(AppConstants.EXTERNAL_STORAGE, recipeDetailsItem.getImageLocation());
                        if (recipeTypeImageFile.exists()) {
                            String imageUri = "file://" + recipeTypeImageFile.getAbsolutePath();
                            Util.imageLoader.displayImage(imageUri, imageViewRecipe);
                        } else {
                            Bitmap placeholder = BitmapFactory.decodeResource(getResources(), R.drawable.recipe_logo_sample);
                            imageViewRecipe.setImageBitmap(placeholder);
                        }
                    } else {
                        Bitmap placeholder = BitmapFactory.decodeResource(getResources(), R.drawable.recipe_logo_sample);
                        imageViewRecipe.setImageBitmap(placeholder);
                    }
                }

                List<RecipeIngredients> recipeIngredients = daoSession.getRecipeIngredientsDao().queryBuilder().where(RecipeIngredientsDao.Properties.DetailsUuid.eq(detailsUuid)).list();
                final List<RecipeSteps> recipeSteps = daoSession.getRecipeStepsDao().queryBuilder().where(RecipeStepsDao.Properties.DetailsUuid.eq(detailsUuid)).list();

                if (!recipeIngredients.isEmpty()) {
                    initIngredients(linearLayoutIngredients, recipeIngredients);
                }

                if (!recipeSteps.isEmpty()) {
                    initSteps(linearLayoutSteps, recipeSteps);
                }

                floatingActionButtonAddRecipe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String recipeName = editTextRecipeName.getText().toString();
                        String recipeDescription = editTextDescription.getText().toString();

                        if (!recipeType.equals("") && !recipeName.equals("") && !recipeDescription.equals("")) {
                            RecipeDetails recipeDetailsNew = recipeDetails.get(0);
                            recipeDetailsNew.setName(recipeName);
                            recipeDetailsNew.setDescription(recipeDescription);
                            daoSession.getRecipeDetailsDao().update(recipeDetailsNew);

                            Toast.makeText(RecipeChosenDetailsActivity.this, "Recipe successfully updated!", Toast.LENGTH_LONG).show();

                            populateRecipeDetailsData(daoSession, detailsUuid);

                            dialogBuilder.dismiss();

                        } else {
                            Toast.makeText(RecipeChosenDetailsActivity.this, "Please fill out the missing field(s).", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                buttonAddIngredients.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String ingredient = editTextAddIngredients.getText().toString();

                        if(!ingredient.equals("")) {
                            RecipeIngredients recipeIngredientsItem = new RecipeIngredients();
                            recipeIngredientsItem.setUuid(Util.getLocalUuid());
                            recipeIngredientsItem.setDetailsUuid(detailsUuid);
                            recipeIngredientsItem.setValue(ingredient);
                            recipeIngredientsItem.setCreatedAt(new Date());
                            recipeIngredientsItem.setUpdatedAt(new Date());

                            daoSession.getRecipeIngredientsDao().insert(recipeIngredientsItem);

                            List<RecipeIngredients> recipeIngredients = daoSession.getRecipeIngredientsDao().queryBuilder().where(RecipeIngredientsDao.Properties.DetailsUuid.eq(detailsUuid)).list();
                            initIngredients(linearLayoutIngredients, recipeIngredients);

                            editTextAddIngredients.setText("");
                        } else {
                            Toast.makeText(RecipeChosenDetailsActivity.this, "Please fill out the ingredient field.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                buttonAddStep.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String step = editTextAddStep.getText().toString();

                        if(!step.equals("")) {
                            RecipeSteps recipeStepsItem = new RecipeSteps();
                            recipeStepsItem.setUuid(Util.getLocalUuid());
                            recipeStepsItem.setDetailsUuid(detailsUuid);
                            recipeStepsItem.setValue(step);
                            recipeStepsItem.setNumber(recipeSteps.size());
                            recipeStepsItem.setCreatedAt(new Date());
                            recipeStepsItem.setUpdatedAt(new Date());

                            daoSession.getRecipeStepsDao().insert(recipeStepsItem);

                            final List<RecipeSteps> recipeSteps = daoSession.getRecipeStepsDao().queryBuilder().where(RecipeStepsDao.Properties.DetailsUuid.eq(detailsUuid)).list();
                            initSteps(linearLayoutSteps, recipeSteps);

                            editTextAddStep.setText("");
                        } else {
                            Toast.makeText(RecipeChosenDetailsActivity.this, "Please fill out the step field.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                imageViewClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogBuilder.dismiss();
                    }
                });
            }
        });
    }

    private void initIngredients(LinearLayout linearLayout, List<RecipeIngredients> recipeIngredients) {
        linearLayout.removeAllViews();

        for (RecipeIngredients item : recipeIngredients) {
            LinearLayout linearLayoutItem = (LinearLayout) getLayoutInflater().inflate(R.layout.ingredient_item, null);
            TextView textView = linearLayoutItem.findViewById(R.id.textViewValue);

            textView.setText("* " + item.getValue());
            linearLayout.addView(linearLayoutItem);
        }
    }

    private void initSteps(LinearLayout linearLayout, List<RecipeSteps> steps) {
        linearLayout.removeAllViews();

        for (RecipeSteps item : steps) {
            LinearLayout linearLayoutItem = (LinearLayout) getLayoutInflater().inflate(R.layout.instruction_item, null);
            TextView textView = linearLayoutItem.findViewById(R.id.textViewValue);

            textView.setText("* " + item.getValue());

            linearLayout.addView(linearLayoutItem);
        }
    }
}
