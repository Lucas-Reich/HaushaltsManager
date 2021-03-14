package com.example.lucas.haushaltsmanager.Database.Migrations;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;

import java.util.Calendar;

import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.RECURRING_BOOKINGS_COL_AMOUNT;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.RECURRING_BOOKINGS_COL_CALENDAR_FIELD;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.RECURRING_BOOKINGS_COL_END;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.RECURRING_BOOKINGS_COL_OCCURRENCE;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_RECURRING_BOOKINGS;

/**
 * In this migration the old RecurringBookings table will be replaced by a new one with a slightly different schema.
 * <p>
 * Columns CALENDAR_FIELD and AMOUNT will be added
 * Column FREQUENCY will be removed
 */
final class V2__Add_New_Columns_To_Recurring_Bookings_Table implements IMigration {
    private static final String TAG = V2__Add_New_Columns_To_Recurring_Bookings_Table.class.getSimpleName();

    private static final String CREATE_RECURRING_BOOKINGS = "CREATE TABLE " + TABLE_RECURRING_BOOKINGS
            + "("
            + RECURRING_BOOKINGS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RECURRING_BOOKINGS_COL_CALENDAR_FIELD + " INTEGER NOT NULL, "
            + RECURRING_BOOKINGS_COL_AMOUNT + " INTEGER NOT NULL, "
            + RECURRING_BOOKINGS_COL_BOOKING_ID + " INTEGER NOT NULL, "
            + RECURRING_BOOKINGS_COL_OCCURRENCE + " TEXT NOT NULL, "
            + RECURRING_BOOKINGS_COL_END + " TEXT NOT NULL"
            + ");";

    private static final String TABLE_RECURRING_BOOKINGS_TEMP = "RECURRING_BOOKINGS_TEMP";
    private static final String RENAME_RECURRING_BOOKINGS_TABLE_TO_TEMP = "ALTER TABLE "
            + TABLE_RECURRING_BOOKINGS
            + " RENAME TO " + TABLE_RECURRING_BOOKINGS_TEMP;


    private static final String DROP_TEMP_RECURRING_BOOKINGS_TABLE = "DROP TABLE IF EXISTS "
            + TABLE_RECURRING_BOOKINGS_TEMP;

    private static final String MIGRATE_DATA_FROM_TEMP_TO_NEW_TABLE = "INSERT INTO "
            + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
            + "("
            + RECURRING_BOOKINGS_COL_CALENDAR_FIELD + ","
            + RECURRING_BOOKINGS_COL_AMOUNT + ","
            + RECURRING_BOOKINGS_COL_BOOKING_ID + ","
            + RECURRING_BOOKINGS_COL_OCCURRENCE + ","
            + RECURRING_BOOKINGS_COL_END
            + ")"
            + " SELECT "
            + Calendar.HOUR + ","
            + RECURRING_BOOKINGS_COL_FREQUENCY + ","
            + RECURRING_BOOKINGS_COL_BOOKING_ID + ","
            + RECURRING_BOOKINGS_COL_OCCURRENCE + ","
            + RECURRING_BOOKINGS_COL_END
            + " FROM " + TABLE_RECURRING_BOOKINGS_TEMP;

    @Override
    public void apply(SQLiteDatabase db) {
        renameRecurringBookingsTable(db);

        createNewRecurringBookingsTable(db);

        migrateRecurringBookingData(db);

        deleteTempRecurringBookingTable(db);
    }

    private void renameRecurringBookingsTable(SQLiteDatabase db) {
        Log.d(TAG, "Rename RecurringBookings table");
        db.execSQL(RENAME_RECURRING_BOOKINGS_TABLE_TO_TEMP);
    }

    private void createNewRecurringBookingsTable(SQLiteDatabase db) {
        Log.d(TAG, "Creating RecurringBookings table with new schema");
        db.execSQL(CREATE_RECURRING_BOOKINGS);
    }

    private void migrateRecurringBookingData(SQLiteDatabase db) {
        Log.d(TAG, "Migrating RecurringBooking data");
        db.execSQL(MIGRATE_DATA_FROM_TEMP_TO_NEW_TABLE);
    }

    private void deleteTempRecurringBookingTable(SQLiteDatabase db) {
        Log.d(TAG, "Deleting temporary RecurringBookings table");
        db.execSQL(DROP_TEMP_RECURRING_BOOKINGS_TABLE);
    }
}
