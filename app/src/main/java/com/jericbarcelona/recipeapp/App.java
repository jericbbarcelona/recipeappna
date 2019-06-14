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

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? AppConstants.DB_ENCRYPTED : AppConstants.DB_NOT_ENCRYPTED);
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb(AppConstants.DB_PASSWORD) : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
