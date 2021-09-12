package com.example.lucas.haushaltsmanager.Database.Migrations;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.entities.Booking.ExpenseType;
import com.example.lucas.haushaltsmanager.entities.Color;
import com.example.lucas.haushaltsmanager.R;

final class V1__Initial_Database_Creation implements IMigration {
    private static final String TAG = V1__Initial_Database_Creation.class.getSimpleName();

    private static final String CREATE_CATEGORIES = "CREATE TABLE CATEGORIES( "
            + "id CHARACTER(36) PRIMARY KEY, "
            + "name TEXT NOT NULL, "
            + "color TEXT NOT NULL, "
            + "default_expense_type INTEGER NOT NULL,"
            + "hidden BOOLEAN NOT NULL)";

    private static final String CREATE_ACCOUNTS = "CREATE TABLE ACCOUNTS( "
            + "id CHARACTER(36) PRIMARY KEY, "
            + "name TEXT NOT NULL, "
            + "balance INTEGER)";

    private static final String CREATE_BOOKINGS = "CREATE TABLE BOOKINGS( "
            + "id TEXT PRIMARY KEY, "
            + "created_at DEFAULT CURRENT_TIMESTAMP, "
            + "expense_type TEXT NOT NULL, "
            + "price REAL NOT NULL, "
            + "expenditure INTEGER NOT NULL, "
            + "title TEXT NOT NULL, "
            + "date TEXT NOT NULL, "
            + "notice TEXT DEFAULT '', "
            + "hidden INTEGER NOT NULL, "
            + "parent_id INTEGER, "
            + "category_id TEXT DEFAULT NULL, "
            + "account_id TEXT DEFAULT NULL, "
            + "FOREIGN KEY (category_id) REFERENCES CATEGORIES(id) ON UPDATE CASCADE ON DELETE RESTRICT, "
            + "FOREIGN KEY (account_id) REFERENCES ACCOUNTS(id) ON UPDATE CASCADE ON DELETE RESTRICT)";

    private static final String CREATE_RECURRING_BOOKINGS = "CREATE TABLE RECURRING_BOOKINGS( "
            + "id CHARACTER(36) PRIMARY KEY, "
            + "created_at DEFAULT CURRENT_TIMESTAMP, "
            + "expense_type TEXT NOT NULL, "
            + "price REAL NOT NULL, "
            + "expenditure INTEGER NOT NULL, "
            + "title TEXT NOT NULL, "
            + "date TEXT NOT NULL, "
            + "calendar_field INTEGER NOT NULL, "
            + "amount INTEGER NOT NULL, "
            + "start TEXT NOT NULL, "
            + "end TEXT NOT NULL, "
            + "category_id CHARACTER(36) NOT NULL, "
            + "account_id CHARACTER(36) NOT NULL, "
            + "FOREIGN KEY (category_id) REFERENCES CATEGORIES (id) ON UPDATE CASCADE ON DELETE RESTRICT, "
            + "FOREIGN KEY (account_id) REFERENCES ACCOUNTS(id) ON UPDATE CASCADE ON DELETE RESTRICT)";

    private static final String CREATE_TEMPLATE_BOOKINGS = "CREATE TABLE TEMPLATE_BOOKINGS( "
            + "id CHARACTER(36) PRIMARY KEY, "
            + "created_at DEFAULT CURRENT_TIMESTAMP, "
            + "expense_type TEXT NOT NULL, "
            + "price REAL NOT NULL, "
            + "expenditure INTEGER NOT NULL, "
            + "title TEXT NOT NULL, "
            + "date TEXT NOT NULL, "
            + "category_id CHARACTER(36) NOT NULL, "
            + "account_id CHARACTER(36) NOT NULL, "
            + "FOREIGN KEY (category_id) REFERENCES CATEGORIES(id) ON UPDATE CASCADE ON DELETE RESTRICT, "
            + "FOREIGN KEY (account_id) REFERENCES ACCOUNTS(id) ON UPDATE CASCADE ON DELETE RESTRICT)";

    public void apply(SQLiteDatabase db) {
        try {
            Log.d(TAG, "Creating BOOKINGS table: " + CREATE_BOOKINGS);
            db.execSQL(CREATE_BOOKINGS);

            Log.d(TAG, "Creating ACCOUNTS table: " + CREATE_ACCOUNTS);
            db.execSQL(CREATE_ACCOUNTS);

            Log.d(TAG, "Creating CATEGORIES table:" + CREATE_CATEGORIES);
            db.execSQL(CREATE_CATEGORIES);

            Log.d(TAG, "Creating TEMPLATE_BOOKINGS table:" + CREATE_TEMPLATE_BOOKINGS);
            db.execSQL(CREATE_TEMPLATE_BOOKINGS);

            Log.d(TAG, "Creating RECURRING_BOOKINGS table:" + CREATE_RECURRING_BOOKINGS);
            db.execSQL(CREATE_RECURRING_BOOKINGS);

            Log.d(TAG, "Creating system categories");
            insertTransferCategory(db);
            insertNotAssignedCategory(db);
        } catch (Exception e) {
            Log.e(TAG, "Fehler beim Anlegen der Tabelle: " + e.getMessage());
        }
    }

    private static void insertTransferCategory(SQLiteDatabase db) {
        Log.d(TAG, "Started inserting hidden categories");
        Color color = new Color(app.getContext().getResources().getColor(R.color.transfer_booking_color));

        ContentValues values = new ContentValues();
        values.put("id", app.transferCategoryId.toString());
        values.put("name", app.getContext().getString(R.string.category_transfer));
        values.put("color", color.getColorString());
        values.put("default_expense_type", ExpenseType.Companion.expense().getType());
        values.put("hidden", 1);
        db.insert("CATEGORIES", null, values);

        Log.d(TAG, "Finished inserting 'Transfer' Category");
    }

    private static void insertNotAssignedCategory(SQLiteDatabase db) {
        Log.d(TAG, "Inserting 'not assigned' Category");
        Color color = new Color(
                app.getContext().getResources().getColor(R.color.unassigned_category_color)
        );

        ContentValues values = new ContentValues();
        values.put("id", app.unassignedCategoryId.toString());
        values.put("name", "Not Assigned");
        values.put("color", color.getColorString());
        values.put("default_expense_type", ExpenseType.Companion.expense().getType());
        values.put("hidden", 1);
        db.insert("CATEGORIES", null, values);

        Log.d(TAG, "Finished inserting 'Not Assigned' Category");

    }
}
