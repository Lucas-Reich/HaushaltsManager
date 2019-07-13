package com.example.lucas.haushaltsmanager.Fixtures;

import android.database.sqlite.SQLiteDatabase;

public interface IFixtures {
    void apply(SQLiteDatabase db);

    void revert(SQLiteDatabase db);
}
