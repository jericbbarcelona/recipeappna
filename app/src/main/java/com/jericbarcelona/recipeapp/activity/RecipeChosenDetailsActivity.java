package com.jericbarcelona.recipeapp.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jericbarcelona.recipeapp.R;
import com.jericbarcelona.recipeapp.model.DaoSession;
import com.jericbarcelona.recipeapp.model.RecipeDetails;
import com.jericbarcelona.recipeapp.model.RecipeDetailsDao;
import com.jericbarcelona.recipeapp.model.RecipeIngredients;
import com.jericbarcelona.recipeapp.model.RecipeIngredientsDao;
import com.jericbarcelona.recipeapp.model.RecipeSteps;
import com.jericbarcelona.recipeapp.model.RecipeStepsDao;
import com.jericbarcelona.recipeapp.util.Util;

import org.w3c.dom.Text;

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
        routeInfoBundle.putString("type_uuid", typeUuid);
        routeInfoBundle.putString("recipe_type", recipeType);
        intent.putExtras(routeInfoBundle);
        startActivity(intent);
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_chosen_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeView();

        Bundle extras = getIntent().getExtras();

        String detailsUuid = extras.getString("details_uuid");
        String detailsName = extras.getString("details_name");
        typeUuid = extras.getString("type_uuid");
        recipeType = extras.getString("recipe_type");

        getSupportActionBar().setTitle(detailsName + " Recipe");

        DaoSession daoSession = Util.getDaoSession(this);
        List<RecipeDetails> recipeDetails = daoSession.getRecipeDetailsDao().queryBuilder().where(RecipeDetailsDao.Properties.Uuid.eq(detailsUuid)).list();

        if(!recipeDetails.isEmpty()) {
            RecipeDetails recipeDetailsItem = recipeDetails.get(0);

            textViewName.setText(recipeDetailsItem.getName());
            textViewDescription.setText(recipeDetailsItem.getDescription());

            Bitmap placeholder = BitmapFactory.decodeResource(getResources(), R.drawable.kaldereta);
            imageViewRecipe.setImageBitmap(placeholder);

            List<RecipeIngredients> recipeIngredients = daoSession.getRecipeIngredientsDao().queryBuilder().where(RecipeIngredientsDao.Properties.DetailsUuid.eq(recipeDetailsItem.getUuid())).list();
            if(!recipeIngredients.isEmpty()) {
                initIngredients(linearLayoutIngredients, recipeIngredients);
            }

            List<RecipeSteps> recipeSteps = daoSession.getRecipeStepsDao().queryBuilder().where(RecipeStepsDao.Properties.DetailsUuid.eq(recipeDetailsItem.getUuid())).list();
            if(!recipeSteps.isEmpty()) {
                initSteps(linearLayoutSteps, recipeSteps);
            }
        }

        floatingButtonEditRecipeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialogBuilder = new Dialog(RecipeChosenDetailsActivity.this);
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
            }
        });
    }

    private void initIngredients(LinearLayout linearLayout, List<RecipeIngredients> recipeIngredients) {
        linearLayout.removeAllViews();

        for(RecipeIngredients item : recipeIngredients) {
            LinearLayout linearLayoutItem = (LinearLayout) getLayoutInflater().inflate(R.layout.ingredient_item, null);
            TextView textView = linearLayoutItem.findViewById(R.id.textViewValue);

            textView.setText("* " + item.getValue());
            linearLayout.addView(linearLayoutItem);
        }
    }

    private void initSteps(LinearLayout linearLayout, List<RecipeSteps> steps) {
        linearLayout.removeAllViews();

        for(RecipeSteps item : steps) {
            LinearLayout linearLayoutItem = (LinearLayout) getLayoutInflater().inflate(R.layout.instruction_item, null);
            TextView textView = linearLayoutItem.findViewById(R.id.textViewValue);

            textView.setText(item.getNumber() + ". " + item.getValue());

            linearLayout.addView(linearLayoutItem);
        }
    }
}
