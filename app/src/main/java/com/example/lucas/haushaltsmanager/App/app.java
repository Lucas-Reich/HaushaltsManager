package com.example.lucas.haushaltsmanager.App;

import android.app.Application;
import android.content.Context;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;

public class app extends Application {
    private static Context context;
    private static ExpensesDbHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        dbHelper = new ExpensesDbHelper();
        DatabaseManager.initializeInstance(dbHelper);
    }

    public static Context getContext() {
        return context;
    }
}
