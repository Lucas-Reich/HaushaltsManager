package com.example.lucas.haushaltsmanager.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.Common.DefaultDatabase;
import com.example.lucas.haushaltsmanager.Fixtures.IFixtures;

import java.util.ArrayList;
import java.util.List;

public abstract class DatabaseTest {
    private List<IFixtures> fixtures = new ArrayList<>();
    private SQLiteDatabase db;

    public DatabaseTest() {
        db = getWritableDatabase();
    }

    protected DefaultDatabase getDefaultDatabase() {
        return new DefaultDatabase(db);
    }

    protected void insertFixtures(List<IFixtures> fixtures) {
        for (IFixtures fixture : fixtures) {
            fixture.apply(db);
        }

        this.fixtures = fixtures;
    }

    public abstract Context getContext();

    protected void clearTables() {
        for (IFixtures fixture : fixtures) {
            fixture.revert(db);
        }
    }

    private SQLiteDatabase getWritableDatabase() {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(getContext()));

        return DatabaseManager.getInstance().openDatabase();
    }
}
