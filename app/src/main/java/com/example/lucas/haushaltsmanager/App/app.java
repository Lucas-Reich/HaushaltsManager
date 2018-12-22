package com.example.lucas.haushaltsmanager.App;

import android.app.Application;
import android.content.Context;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;

public class app extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();

        initializeDatabase();
    }

    private void initializeDatabase() {
        DatabaseManager.initializeInstance(new ExpensesDbHelper());
    }

    public static Context getContext() {
        return mContext;
    }
}
