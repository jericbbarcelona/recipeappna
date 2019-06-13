package com.jericbarcelona.recipeapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jericbarcelona.recipeapp.util.Util;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Util.loadRecipeDataFromJson(this);
    }
}
