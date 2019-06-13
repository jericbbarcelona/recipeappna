package com.jericbarcelona.recipeapp;

import android.app.Application;

import com.jericbarcelona.recipeapp.model.DaoMaster;
import com.jericbarcelona.recipeapp.model.DaoSession;

import org.greenrobot.greendao.database.Database;

public class App extends Application {

    public static final boolean ENCRYPTED = false;
    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "recipe_app_encrypted" : "recipe_app");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("wingan1234") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
