package com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.Exceptions.RecurringBookingNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.RecurringBooking;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecurringBookingRepository {
    private static final String TAG = RecurringBookingRepository.class.getSimpleName();
    private SQLiteDatabase mDatabase;
    private ExpenseRepository mBookingRepo;

    public RecurringBookingRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
        mBookingRepo = new ExpenseRepository(context);
    }

    public boolean exists(RecurringBooking recurringBooking) {
        String selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS + "." + ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + " = " + recurringBooking.getIndex()
                + " AND " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS + "." + ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID + " = " + recurringBooking.getExpense().getIndex()
                + " AND " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS + "." + ExpensesDbHelper.RECURRING_BOOKINGS_COL_START + " = " + recurringBooking.getStart().getTimeInMillis()
                + " AND " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS + "." + ExpensesDbHelper.RECURRING_BOOKINGS_COL_END + " = " + recurringBooking.getEnd().getTimeInMillis()
                + " AND " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS + "." + ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY + " = " + recurringBooking.getFrequency()
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
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID, recurringBooking.getExpense().getIndex());
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_START, recurringBooking.getStart().getTimeInMillis());
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY, recurringBooking.getFrequency());
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_END, recurringBooking.getEnd().getTimeInMillis());

        long insertedIndex = mDatabase.insert(ExpensesDbHelper.TABLE_RECURRING_BOOKINGS, null, values);

        return new RecurringBooking(
                insertedIndex,
                recurringBooking.getStart(),
                recurringBooking.getEnd(),
                recurringBooking.getFrequency(),
                recurringBooking.getExpense()
        );
    }

    public RecurringBooking get(long index) throws RecurringBookingNotFoundException {
        String selectQuery = "SELECT "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_START + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_END
                + " FROM " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
                + " WHERE " + ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + " = " + index;

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (!c.moveToFirst())
            throw new RecurringBookingNotFoundException(index);

        return fromCursor(c);
    }

    public List<RecurringBooking> getAll(Calendar start, Calendar end) {
        String selectQuery = "SELECT "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_START + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_END
                + " FROM " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
                + " WHERE " + ExpensesDbHelper.RECURRING_BOOKINGS_COL_END + " > " + start.getTimeInMillis() + ";";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        List<RecurringBooking> recurringBookings = new ArrayList<>();
        while (c.moveToNext()) {
            RecurringBooking expense = fromCursor(c);

            if (expense.occursInRange(start, end))
                recurringBookings.add(expense);
        }

        return recurringBookings;
    }

    public boolean update(RecurringBooking recurringBooking) {
        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID, recurringBooking.getExpense().getIndex());
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_START, recurringBooking.getStart().getTimeInMillis());
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY, recurringBooking.getFrequency());
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
                ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + "= ?",
                new String[]{"" + recurringBooking.getIndex()}
        );
    }

    private RecurringBooking fromCursor(Cursor c) {
        long index = c.getLong(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID));

        long startInMillis = c.getLong(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_START));
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(startInMillis);

        long endInMillis = c.getLong(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_END));
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(endInMillis);

        int frequencyInHours = c.getInt(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY));

        //TODO kann man das auch irgendwie anders handeln?
        ExpenseObject expense = null;
        long expenseId = c.getLong(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID));
        try {

            expense = mBookingRepo.get(expenseId);
        } catch (ExpenseNotFoundException e) {

            Log.e(TAG, "Failed to fetch Expense " + expenseId, e);
        }

        if (c.isLast())
            c.close();

        return new RecurringBooking(
                index,
                start,
                end,
                frequencyInHours,
                expense
        );
    }
}
