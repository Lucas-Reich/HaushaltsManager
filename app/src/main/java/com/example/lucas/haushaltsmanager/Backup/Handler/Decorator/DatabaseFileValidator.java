package com.example.lucas.haushaltsmanager.Backup.Handler.Decorator;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.example.lucas.haushaltsmanager.Backup.Exceptions.SQLiteOpenDatabaseFileException;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidFileException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseFileValidator {

    public void guardAgainstInvalidDatabaseSchema(File database) throws SQLiteOpenDatabaseFileException {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(database.getPath(), null, 0);
            Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
            c.moveToFirst();

            List<String> tableName = new ArrayList<>();
            while (!c.isAfterLast()) {
                tableName.add(c.getString(c.getColumnIndex("name")));
                c.moveToNext();
            }
            db.close();
            c.close();

            boolean hasValidSchema = validateSchema(tableName);

            if (hasValidSchema) {
                return;
            }

            throw SQLiteOpenDatabaseFileException.invalidSchema(database);
        } catch (SQLiteException e) {
            throw SQLiteOpenDatabaseFileException.generic(database);
        }
    }

    public void guardAgainstWrongDatabaseVersion(File database) throws SQLiteOpenDatabaseFileException {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(database.getPath(), null, 0);
            Cursor c = db.rawQuery("PRAGMA user_version;", null);
            c.moveToFirst();
            int dbVersion = c.getInt(c.getColumnIndex("user_version"));
            c.close();
            db.close();

            if (ExpensesDbHelper.DB_VERSION == dbVersion) {
                return;
            }

            throw SQLiteOpenDatabaseFileException.invalidVersion(ExpensesDbHelper.DB_VERSION, dbVersion);
        } catch (SQLiteException e) {
            throw SQLiteOpenDatabaseFileException.invalidVersion(ExpensesDbHelper.DB_VERSION, -1);
        }
    }

    /**
     * Überprüft ob die angegebene Datei eine SQLite Datenbank enthält.
     * Quelle: https://stackoverflow.com/a/39751165/9376633
     */
    public void guardAgainstNoDatabase(File file) throws SQLiteOpenDatabaseFileException, InvalidFileException {
        try {
            FileReader fr = new FileReader(file);
            char[] buffer = new char[16];

            fr.read(buffer, 0, 16);
            String str = String.valueOf(buffer);
            fr.close();

            if (str.equals("SQLite format 3\u0000")) {
                return;
            }

            throw SQLiteOpenDatabaseFileException.invalidFile(file);
        } catch (IOException e) {
            throw InvalidFileException.generic(file);
        }
    }

    private boolean validateSchema(List<String> input) {
        return input.contains(ExpensesDbHelper.TABLE_ACCOUNTS)
                && input.contains(ExpensesDbHelper.TABLE_BOOKINGS)
                && input.contains(ExpensesDbHelper.TABLE_BOOKINGS_TAGS)
                && input.contains(ExpensesDbHelper.TABLE_CATEGORIES)
                && input.contains(ExpensesDbHelper.TABLE_CHILD_CATEGORIES)
                && input.contains(ExpensesDbHelper.TABLE_CURRENCIES)
                && input.contains(ExpensesDbHelper.TABLE_RECURRING_BOOKINGS)
                && input.contains(ExpensesDbHelper.TABLE_TAGS)
                && input.contains(ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS);
    }
}
