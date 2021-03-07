package com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.Exceptions.RecurringBookingNotFoundException;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.RecurringBooking;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecurringBookingRepository {
    private SQLiteDatabase mDatabase;
    private final TransformerInterface<RecurringBooking> transformer;

    public RecurringBookingRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
        transformer = new RecurringBookingTransformer(
                new ExpenseRepository(context)
        );
    }

    // TODO: This method is only used within tests
    public boolean exists(RecurringBooking recurringBooking) {
        Cursor c = executeRaw(new RecurringBookingExistsQuery(recurringBooking));

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    public boolean exists(ExpenseObject expense) {
        Cursor c = executeRaw(new BookingExistsQuery(expense));

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
        Cursor c = executeRaw(new GetRecurringBookingQuery(index));

        if (!c.moveToFirst())
            throw new RecurringBookingNotFoundException(index);

        return transformer.transform(c);
    }

    public List<RecurringBooking> getAll(Calendar start, Calendar end) {
        Cursor c = executeRaw(new GetAllRecurringBookingsQuery(start, end));

        List<RecurringBooking> recurringBookings = new ArrayList<>();
        while (c.moveToNext()) {
            recurringBookings.add(transformer.transform(c));
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

    private Cursor executeRaw(QueryInterface query) {
        return mDatabase.rawQuery(String.format(
                query.sql(),
                query.values()
        ), null);
    }
}
