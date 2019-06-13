package com.jericbarcelona.recipeapp;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jericbarcelona.recipeapp.util.Util;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton floatingActionButtonAddType;

    private void initializeView() {
        floatingActionButtonAddType = (FloatingActionButton) findViewById(R.id.floatingButtonAddType);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initializeView();

        Util.loadRecipeDataFromJson(this);

        floatingActionButtonAddType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

    }
}
