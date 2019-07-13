package com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings;

import android.database.Cursor;
import android.util.Log;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Common.IQueryResult;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Common.ITransformer;
import com.example.lucas.haushaltsmanager.Entities.Expense.Booking;
import com.example.lucas.haushaltsmanager.Entities.Frequency;
import com.example.lucas.haushaltsmanager.Entities.RecurringBooking;

import java.util.Calendar;

public class RecurringBookingTransformer implements ITransformer<RecurringBooking> {
    private static final String TAG = RecurringBookingTransformer.class.getSimpleName();

    @Override
    public RecurringBooking transform(IQueryResult queryResult) {
        if (!queryResult.moveToNext()) {
            return null;
        }

        return fromCursor(queryResult.getCurrent());
    }

    @Override
    public RecurringBooking transformAndClose(IQueryResult queryResult) {
        RecurringBooking recurringBooking = transform(queryResult);

        queryResult.close();

        return recurringBooking;
    }

    private RecurringBooking fromCursor(Cursor c) {
        long index = c.getLong(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID));
        long startInMillis = c.getLong(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_OCCURRENCE));
        long endInMillis = c.getLong(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_END));

        return RecurringBooking.load(
                index,
                createFromMillis(startInMillis),
                createFromMillis(endInMillis),
                getFrequency(c),
                getBooking(c)
        );
    }

    private Booking getBooking(Cursor c) {
        long id = c.getLong(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID));

        ExpenseRepository bookingRepo = new ExpenseRepository(app.getContext());

        try {

            return bookingRepo.get(id);
        } catch (ExpenseNotFoundException e) {

            Log.e(TAG, "Failed to fetch Booking " + id, e);
        }

        return null;
    }

    private Frequency getFrequency(Cursor c) {
        int calendarField = c.getInt(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_CALENDAR_FIELD));
        int amount = c.getInt(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_AMOUNT));

        return new Frequency(
                calendarField,
                amount
        );
    }

    private Calendar createFromMillis(long millis) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(millis);

        return date;
    }
}
