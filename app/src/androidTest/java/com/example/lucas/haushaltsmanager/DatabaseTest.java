package com.example.lucas.haushaltsmanager;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Fixtures.IFixtures;

public abstract class DatabaseTest {
    protected void clearTable(String tableName) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(
                tableName,
                null, // If where clause is null it will Delete all entries from table
                null
        );
    }

    protected void createFixtures(String tableName, String fixtureName) {
        SQLiteDatabase database = getWritableDatabase();

        IFixtures fixtures = FixtureFactory.create(fixtureName);
        for (ContentValues values : fixtures.getContentValues()) {
            database.insert(
                    tableName,
                    null,
                    values
            );
        }
    }

    private SQLiteDatabase getWritableDatabase() {
        DatabaseManager.initializeInstance(new ExpensesDbHelper());

        return DatabaseManager.getInstance().openDatabase();
    }
}
