package com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings;

import android.database.Cursor;
import android.util.Log;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.ExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Expense.Booking;
import com.example.lucas.haushaltsmanager.Entities.Frequency;
import com.example.lucas.haushaltsmanager.Entities.RecurringBooking;

import java.util.Calendar;

public class RecurringBookingTransformer implements TransformerInterface<RecurringBooking> {
    private static final String TAG = RecurringBookingTransformer.class.getSimpleName();
    private final ExpenseRepository expenseRepository;

    public RecurringBookingTransformer(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public RecurringBooking transform(Cursor c) {
        if (c.isAfterLast()) {
            return null;
        }

        return fromCursor(c);
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

        try {

            return expenseRepository.get(id);
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
