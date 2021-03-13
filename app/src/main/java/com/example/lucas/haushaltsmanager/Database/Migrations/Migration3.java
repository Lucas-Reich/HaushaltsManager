package com.example.lucas.haushaltsmanager.Database.Migrations;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.ACCOUNTS_COL_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_CREATED_AT;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_DATE;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_HIDDEN;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_NOTICE;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_PARENT_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_PRICE;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_TITLE;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CHILD_CATEGORIES_COL_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CURRENCIES_COL_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_ACCOUNTS;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_BOOKINGS;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_CHILD_CATEGORIES;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_CURRENCIES;

final class Migration3 implements IMigration {
    private static final String TAG = Migration3.class.getSimpleName();
    private static final String TABLE_BOOKINGS_NEW = "BOOKINGS_new";

    private static final String CREATE_BOOKINGS_TABLE_FOREIGN_KEY = "CREATE TABLE " + TABLE_BOOKINGS_NEW
            + "("
            + BOOKINGS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + BOOKINGS_COL_CREATED_AT + " DEFAULT CURRENT_TIMESTAMP, "
            + BOOKINGS_COL_EXPENSE_TYPE + " TEXT NOT NULL, "
            + BOOKINGS_COL_PRICE + " REAL NOT NULL, "
            + BOOKINGS_COL_CATEGORY_ID + " INTEGER NOT NULL, "
            + BOOKINGS_COL_EXPENDITURE + " INTEGER NOT NULL, "
            + BOOKINGS_COL_TITLE + " TEXT NOT NULL, "
            + BOOKINGS_COL_DATE + " TEXT NOT NULL, "
            + BOOKINGS_COL_NOTICE + " TEXT, "
            + BOOKINGS_COL_ACCOUNT_ID + " INTEGER, "
            + BOOKINGS_COL_CURRENCY_ID + " INTEGER NOT NULL, "
            + BOOKINGS_COL_HIDDEN + " INTEGER NOT NULL, "
            + BOOKINGS_COL_PARENT_ID + " INTEGER, "
            + "FOREIGN KEY (" + BOOKINGS_COL_CATEGORY_ID + ") REFERENCES " + TABLE_CHILD_CATEGORIES + "(" + CHILD_CATEGORIES_COL_ID + ") ON UPDATE CASCADE ON DELETE RESTRICT, "
            + "FOREIGN KEY (" + BOOKINGS_COL_ACCOUNT_ID + ") REFERENCES " + TABLE_ACCOUNTS + "(" + ACCOUNTS_COL_ID + ") ON UPDATE CASCADE ON DELETE RESTRICT, "
            + "FOREIGN KEY (" + BOOKINGS_COL_CURRENCY_ID + ") REFERENCES " + TABLE_CURRENCIES + "(" + CURRENCIES_COL_ID + ") ON UPDATE CASCADE ON DELETE RESTRICT"
            + ");";

    @Override
    public void apply(SQLiteDatabase db) {
        createBookingsTableWithForeignKeys(db);

        copyDataFromOldTable(db);

        deleteOldBookingsTable(db);

        renameNewBookingsTable(db);
    }

    @Override
    public void revert(SQLiteDatabase db) {
        // Revert changes from this migration
    }

    private void createBookingsTableWithForeignKeys(SQLiteDatabase db) {
        Log.d(TAG, "Creating new Bookings table with foreign key support");
        db.execSQL(CREATE_BOOKINGS_TABLE_FOREIGN_KEY);
    }

    private void copyDataFromOldTable(SQLiteDatabase db) {
        Log.d(TAG, "Copying data from old Bookings table to new");

        db.execSQL(String.format("INSERT INTO %s SELECT * FROM %s;",
                TABLE_BOOKINGS_NEW,
                TABLE_BOOKINGS
        ));
    }

    private void deleteOldBookingsTable(SQLiteDatabase db) {
        Log.d(TAG, "Deleting old Bookings table");

        db.execSQL(String.format("DROP TABLE %s;",
                TABLE_BOOKINGS
        ));
    }

    private void renameNewBookingsTable(SQLiteDatabase db) {
        Log.d(TAG, "Renaming new Bookings table");

        db.execSQL(String.format("ALTER TABLE %s RENAME TO %s;",
                TABLE_BOOKINGS_NEW,
                TABLE_BOOKINGS
        ));
    }
}
