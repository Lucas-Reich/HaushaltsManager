package com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.Common.ITransformer;
import com.example.lucas.haushaltsmanager.Database.Common.QueryResult;
import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.Exceptions.RecurringBookingNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.RecurringBooking;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecurringBookingRepository {
    private SQLiteDatabase mDatabase;
    private ITransformer<RecurringBooking> transformer;

    public RecurringBookingRepository(Context context) {
        // TODO: Kann ich hier eine Default database injecten, sodass es mit dem testen einfacher wird
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
        transformer = new RecurringBookingTransformer();
    }

    public boolean exists(RecurringBooking recurringBooking) {
        String selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS + "." + ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + " = " + recurringBooking.getIndex()
                + " AND " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS + "." + ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID + " = " + recurringBooking.getBooking().getIndex()
                + " AND " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS + "." + ExpensesDbHelper.RECURRING_BOOKINGS_COL_OCCURRENCE + " = " + recurringBooking.getExecutionDate().getTimeInMillis()
                + " AND " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS + "." + ExpensesDbHelper.RECURRING_BOOKINGS_COL_END + " = " + recurringBooking.getEnd().getTimeInMillis()
                + " AND " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS + "." + ExpensesDbHelper.RECURRING_BOOKINGS_COL_CALENDAR_FIELD + " = " + recurringBooking.getFrequency().getCalendarField()
                + " AND " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS + "." + ExpensesDbHelper.RECURRING_BOOKINGS_COL_AMOUNT + " = " + recurringBooking.getFrequency().getAmount()
                + " LIMIT 1;";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    public boolean exists(ExpenseObject expense) {
        String selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS + "." + ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID + " = " + expense.getIndex()
                + " LIMIT 1;";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    public RecurringBooking create(RecurringBooking recurringBooking) {
        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID, recurringBooking.getBooking().getIndex());
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_OCCURRENCE, recurringBooking.getExecutionDate().getTimeInMillis());
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_CALENDAR_FIELD, recurringBooking.getFrequency().getCalendarField());
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_AMOUNT, recurringBooking.getFrequency().getAmount());
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_END, recurringBooking.getEnd().getTimeInMillis());

        long insertedIndex = mDatabase.insert(ExpensesDbHelper.TABLE_RECURRING_BOOKINGS, null, values);

        return RecurringBooking.load(
                insertedIndex,
                recurringBooking.getExecutionDate(),
                recurringBooking.getEnd(),
                recurringBooking.getFrequency(),
                recurringBooking.getBooking()
        );
    }

    public RecurringBooking get(long index) throws RecurringBookingNotFoundException {
        String selectQuery = "SELECT "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_CALENDAR_FIELD + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_AMOUNT + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_OCCURRENCE + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_END
                + " FROM " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
                + " WHERE " + ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + " = " + index;

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (!c.moveToFirst())
            throw new RecurringBookingNotFoundException(index);

        return transformer.transform(new QueryResult(c));
    }

    public List<RecurringBooking> getAll(Calendar start, Calendar end) {
        String selectQuery = "SELECT "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_CALENDAR_FIELD + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_AMOUNT + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_OCCURRENCE + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_END
                + " FROM " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
                + " WHERE " + ExpensesDbHelper.RECURRING_BOOKINGS_COL_OCCURRENCE
                + " BETWEEN " + start.getTimeInMillis() + " AND " + end.getTimeInMillis() + ";";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        List<RecurringBooking> recurringBookings = new ArrayList<>();
        while (c.moveToNext()) {
            recurringBookings.add(transformer.transform(new QueryResult(c)));
        }

        c.close();
        return recurringBookings;
    }

    public boolean update(RecurringBooking recurringBooking) {
        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID, recurringBooking.getBooking().getIndex());
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_OCCURRENCE, recurringBooking.getExecutionDate().getTimeInMillis());
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_CALENDAR_FIELD, recurringBooking.getFrequency().getCalendarField());
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_AMOUNT, recurringBooking.getFrequency().getAmount());
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_END, recurringBooking.getEnd().getTimeInMillis());

        return 1 == mDatabase.update(
                ExpensesDbHelper.TABLE_RECURRING_BOOKINGS,
                values,
                ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + " = ?",
                new String[]{"" + recurringBooking.getIndex()}
        );
    }

    public void delete(RecurringBooking recurringBooking) {
        mDatabase.delete(
                ExpensesDbHelper.TABLE_RECURRING_BOOKINGS,
                ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + " = ?",
                new String[]{"" + recurringBooking.getIndex()}
        );
    }
}
