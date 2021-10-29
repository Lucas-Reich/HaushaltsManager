package com.example.lucas.haushaltsmanager.Database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager {
    private static DatabaseManager instance;
    private static SQLiteOpenHelper mDatabaseHelper;
    private Integer mOpenCounter = 0;
    private SQLiteDatabase mDatabase;

    public static synchronized void initializeInstance(ExpensesDbHelper helper) {
        if (instance == null) {
            instance = new DatabaseManager();
            mDatabaseHelper = helper;
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(DatabaseManager.class.getSimpleName() + " is not initialized, call initializeInstance(..) method first.");
        }

        return instance;
    }

    public static synchronized void clearInstance() {
        if (instance != null) {
            instance = null;

            mDatabaseHelper.close();
        }
    }

    public synchronized SQLiteDatabase openDatabase() {
        mOpenCounter += 1;
        if (mOpenCounter == 1) {
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }

        return mDatabase;
    }
}
