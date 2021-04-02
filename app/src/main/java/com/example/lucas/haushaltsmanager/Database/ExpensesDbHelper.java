package com.example.lucas.haushaltsmanager.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.lucas.haushaltsmanager.Database.Migrations.IMigration;
import com.example.lucas.haushaltsmanager.Database.Migrations.MigrationHelper;

public class ExpensesDbHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "expenses.db";
    public static final int DB_VERSION = 1;

    public ExpensesDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        IMigration[] migrations = MigrationHelper.getMigrations();

        for (IMigration migration : migrations) {
            migration.apply(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        IMigration[] migrations = MigrationHelper.getMigrations();

        for (int i = oldVersion; i < newVersion; i++) {
            migrations[i].apply(db);
        }
    }
}
