package com.example.lucas.haushaltsmanager.Database.Migrations;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.ACCOUNTS_COL_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CHILD_CATEGORIES_COL_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CURRENCIES_COL_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_ACCOUNTS;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_CHILD_CATEGORIES;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_CURRENCIES;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_ACCOUNT_ID;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_CATEGORY_ID;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_CREATED_AT;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_CURRENCY_ID;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_DATE;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_EXPENDITURE;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_EXPENSE_TYPE;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_ID;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_PRICE;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_TITLE;

final class V6__Recreate_Template_Bookings_Table implements IMigration {
    private final String TAG = V6__Recreate_Template_Bookings_Table.class.getCanonicalName();
    private final String TABLE_TEMPLATE_BOOKINGS_NEW = TABLE_TEMPLATE_BOOKINGS + "_new";

    private final String CREATE_FULL_TEMPLATE_BOOKINGS = "CREATE TABLE " + TABLE_TEMPLATE_BOOKINGS_NEW
            + "("
            + TB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TB_CREATED_AT + " DEFAULT CURRENT_TIMESTAMP, "
            + TB_EXPENSE_TYPE + " TEXT NOT NULL, "
            + TB_PRICE + " REAL NOT NULL, "
            + TB_CATEGORY_ID + " INTEGER NOT NULL, "
            + TB_EXPENDITURE + " INTEGER NOT NULL, "
            + TB_TITLE + " TEXT NOT NULL, "
            + TB_DATE + " TEXT NOT NULL, "
            + TB_ACCOUNT_ID + " INTEGER NOT NULL, "
            + TB_CURRENCY_ID + " INTEGER NOT NULL, "
            + "FOREIGN KEY (" + BOOKINGS_COL_CATEGORY_ID + ") REFERENCES " + TABLE_CHILD_CATEGORIES + "(" + CHILD_CATEGORIES_COL_ID + ") ON UPDATE CASCADE ON DELETE RESTRICT, "
            + "FOREIGN KEY (" + BOOKINGS_COL_ACCOUNT_ID + ") REFERENCES " + TABLE_ACCOUNTS + "(" + ACCOUNTS_COL_ID + ") ON UPDATE CASCADE ON DELETE RESTRICT, "
            + "FOREIGN KEY (" + BOOKINGS_COL_CURRENCY_ID + ") REFERENCES " + TABLE_CURRENCIES + "(" + CURRENCIES_COL_ID + ") ON UPDATE CASCADE ON DELETE RESTRICT"
            + ");";

    @Override
    public void apply(SQLiteDatabase db) {
        createNewTable(db);

        copyData(db);

        deleteOldTable(db);

        renameNewTable(db);
    }

    private void createNewTable(SQLiteDatabase db) {
        Log.d(TAG, "Creating new TemplateBookings table");

        db.execSQL(CREATE_FULL_TEMPLATE_BOOKINGS);
    }

    private void copyData(SQLiteDatabase db) {
        Log.d(TAG, "Copying data from old table");

        db.execSQL("INSERT INTO TEMPLATE_BOOKINGS_NEW (created_at, expense_type, price, category_id, expenditure, title, date, account_id, currency_id) " +
                "SELECT created_at, expense_type, price, category_id, expenditure, title, date, account_id, currency_id " +
                "FROM TEMPLATE_BOOKINGS JOIN BOOKINGS ON TEMPLATE_BOOKINGS.booking_id = BOOKINGS.booking_id;"
        );
    }

    private void deleteOldTable(SQLiteDatabase db) {
        Log.d(TAG, "Deleting old TemplateBookings table");

        db.execSQL(String.format("DROP TABLE %s", TABLE_TEMPLATE_BOOKINGS));
    }

    private void renameNewTable(SQLiteDatabase db) {
        Log.d(TAG, "Renaming new TemplateBookings table");

        db.execSQL(String.format("ALTER TABLE %s RENAME TO %s",
                TABLE_TEMPLATE_BOOKINGS_NEW,
                TABLE_TEMPLATE_BOOKINGS
        ));
    }
}

