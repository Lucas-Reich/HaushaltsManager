package com.example.lucas.haushaltsmanager.Database.Migrations;

import android.database.sqlite.SQLiteDatabase;

public interface IMigration {
    void apply(SQLiteDatabase db);

    void revert(SQLiteDatabase db);
}
