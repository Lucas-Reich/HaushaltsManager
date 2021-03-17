package com.example.lucas.haushaltsmanager.Database.Migrations;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

final class V7__Recreate_Recurring_Bookings_Table implements IMigration {
    private static final String TAG = V7__Recreate_Recurring_Bookings_Table.class.getSimpleName();

    private static final String CREATE_RECURRING_BOOKINGS_TABLE = "CREATE TABLE RECURRING_BOOKINGS_NEW ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "created_at DEFAULT CURRENT_TIMESTAMP, "
            + "expense_type TEXT NOT NULL, "
            + "price REAL NOT NULL, "
            + "category_id INTEGER NOT NULL, "
            + "expenditure INTEGER NOT NULL, "
            + "title TEXT NOT NULL, "
            + "date TEXT NOT NULL, "
            + "account_id INTEGER NOT NULL, "
            + "currency_id INTEGER NOT NULL, "
            + "calendar_field INTEGER NOT NULL, "
            + "amount INTEGER NOT NULL, "
            + "start TEXT NOT NULL, "
            + "end TEXT NOT NULL, "
            + "FOREIGN KEY (category_id) REFERENCES CHILD_CATEGORIES (child_category_id) ON UPDATE CASCADE ON DELETE RESTRICT, "
            + "FOREIGN KEY (account_id) REFERENCES ACCOUNTS (account_id) ON UPDATE CASCADE ON DELETE RESTRICT, "
            + "FOREIGN KEY (currency_id) REFERENCES CURRENCIES (currency_id) ON UPDATE CASCADE ON DELETE RESTRICT)";

    @Override
    public void apply(SQLiteDatabase db) {
        createNewTable(db);

        copyData(db);

        deleteOldTable(db);

        renameNewTable(db);
    }

    private void createNewTable(SQLiteDatabase db) {
        Log.d(TAG, "Creating new RecurringBookings table");

        db.execSQL(CREATE_RECURRING_BOOKINGS_TABLE);
    }

    private void copyData(SQLiteDatabase db) {
        Log.d(TAG, "Copying data from old RecurringBookingsTable to new");

        db.execSQL("INSERT INTO RECURRING_BOOKINGS_NEW (expense_type, price, category_id, expenditure, title, date, account_id, currency_id, calendar_field, amount, start, end) " +
                "SELECT expense_type, price, category_id, expenditure, title, date, account_id, currency_id, calendar_field, amount, start, end " +
                "FROM RECURRING_BOOKINGS JOIN BOOKINGS ON RECURRING_BOOKINGS.booking_id = BOOKINGS.booking_id"
        );
    }

    private void deleteOldTable(SQLiteDatabase db) {
        Log.d(TAG, "Deleting old RecurringBookings table");

        db.execSQL("DROP TABLE RECURRING_BOOKINGS");
    }

    private void renameNewTable(SQLiteDatabase db) {
        Log.d(TAG, "Renaming new RecurringBookings table");

        db.execSQL("ALTER TABLE RECURRING_BOOKINGS_NEW RENAME TO RECURRING_BOOKINGS");
    }
}

