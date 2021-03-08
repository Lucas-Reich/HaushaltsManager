package com.example.lucas.haushaltsmanager.Database.Migrations;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.ACCOUNTS_COL_BALANCE;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.ACCOUNTS_COL_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.ACCOUNTS_COL_NAME;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CURRENCIES_COL_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_ACCOUNTS;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_CURRENCIES;

/**
 * This migration will create a new Accounts table.
 * The new Accounts table will have a new foreign key constraint to the Currencies table.
 */
class Migration2 implements IMigration {
    private static final String TAG = Migration2.class.getSimpleName();

    private static final String TABLE_ACCOUNTS_NEW = "ACCOUNTS_new";

    private static final String CREATE_ACCOUNTS_TABLE_FOREIGN_KEY = "CREATE TABLE " + TABLE_ACCOUNTS_NEW
            + "("
            + ACCOUNTS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ACCOUNTS_COL_NAME + " TEXT NOT NULL, "
            + ACCOUNTS_COL_BALANCE + " INTEGER, "
            + ACCOUNTS_COL_CURRENCY_ID + " INTEGER NOT NULL, "
            + "FOREIGN KEY (" + ACCOUNTS_COL_CURRENCY_ID + ") REFERENCES " + TABLE_CURRENCIES + "(" + CURRENCIES_COL_ID + ") "
            + "ON UPDATE CASCADE ON DELETE RESTRICT"
            + ");";

    @Override
    public void apply(SQLiteDatabase db) {
        createAccountsTableWithForeignKey(db);

        copyDataFromOldTable(db);

        deleteOldAccountsTable(db);

        renameNewAccountsTable(db);
    }

    @Override
    public void revert(SQLiteDatabase db) {
        // Revert changes from this migration
    }

    private void createAccountsTableWithForeignKey(SQLiteDatabase db) {
        Log.d(TAG, "Creating new Accounts table with foreign key support");
        db.execSQL(CREATE_ACCOUNTS_TABLE_FOREIGN_KEY);
    }

    private void copyDataFromOldTable(SQLiteDatabase db) {
        Log.d(TAG, "Copying data from old Accounts table to new");

        db.execSQL(String.format("INSERT INTO %s SELECT * FROM %s;",
                TABLE_ACCOUNTS_NEW,
                TABLE_ACCOUNTS
        ));
    }

    private void deleteOldAccountsTable(SQLiteDatabase db) {
        Log.d(TAG, "Deleting old Accounts table");

        db.execSQL(String.format("DROP TABLE %s;",
                TABLE_ACCOUNTS
        ));
    }

    private void renameNewAccountsTable(SQLiteDatabase db) {
        Log.d(TAG, "Renaming new Accounts table");

        db.execSQL(String.format("ALTER TABLE %s RENAME TO %s;",
                TABLE_ACCOUNTS_NEW,
                TABLE_ACCOUNTS
        ));
    }
}
