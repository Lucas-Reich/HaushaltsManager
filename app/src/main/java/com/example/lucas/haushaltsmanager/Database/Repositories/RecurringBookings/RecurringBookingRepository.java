package com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryTransformer;
import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.Exceptions.RecurringBookingCouldNotBeCreatedException;
import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.Exceptions.RecurringBookingNotFoundException;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.entities.Booking.Booking;
import com.example.lucas.haushaltsmanager.entities.RecurringBooking;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class RecurringBookingRepository {
    private static final String TABLE = "RECURRING_BOOKINGS";

    private SQLiteDatabase database;
    private final TransformerInterface<RecurringBooking> transformer;

    public RecurringBookingRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        database = DatabaseManager.getInstance().openDatabase();
        transformer = new RecurringBookingTransformer(
                new CategoryTransformer()
        );
    }

    public void insert(RecurringBooking recurringBooking) throws RecurringBookingCouldNotBeCreatedException {
        Booking booking = recurringBooking.getBooking();

        ContentValues values = new ContentValues();
        values.put("id", recurringBooking.getId().toString());
        values.put("expense_type", booking.getExpenseType().name());
        values.put("price", booking.getUnsignedPrice());
        values.put("category_id", booking.getCategory().getId().toString());
        values.put("expenditure", booking.getPrice().isNegative() ? 1 : 0);
        values.put("title", booking.getTitle());
        values.put("date", booking.getDate().getTimeInMillis());
        values.put("account_id", booking.getAccountId().toString());

        values.put("calendar_field", recurringBooking.getFrequency().getCalendarField());
        values.put("amount", recurringBooking.getFrequency().getAmount());
        values.put("start", recurringBooking.getExecutionDate().getTimeInMillis());
        values.put("end", recurringBooking.getEnd().getTimeInMillis());

        try {
            database.insertOrThrow(
                    TABLE,
                    null,
                    values
            );
        } catch (SQLException e) {
            throw new RecurringBookingCouldNotBeCreatedException(recurringBooking, e);
        }
    }

    public RecurringBooking get(UUID id) throws RecurringBookingNotFoundException {
        Cursor c = executeRaw(new GetRecurringBookingQuery(id));

        if (!c.moveToFirst()) {
            throw new RecurringBookingNotFoundException(id);
        }

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
        Booking booking = recurringBooking.getBooking();

        ContentValues values = new ContentValues();
        values.put("expense_type", booking.getExpenseType().name());
        values.put("price", booking.getUnsignedPrice());
        values.put("category_id", booking.getCategory().getId().toString());
        values.put("expenditure", booking.getPrice().isNegative() ? 1 : 0);
        values.put("title", booking.getTitle());
        values.put("date", booking.getDate().getTimeInMillis());
        values.put("account_id", booking.getAccountId().toString());

        values.put("calendar_field", recurringBooking.getFrequency().getCalendarField());
        values.put("amount", recurringBooking.getFrequency().getAmount());
        values.put("start", recurringBooking.getExecutionDate().getTimeInMillis());
        values.put("end", recurringBooking.getEnd().getTimeInMillis());

        return 1 == database.update(
                TABLE,
                values,
                "id = ?",
                new String[]{recurringBooking.getId().toString()}
        );
    }

    public void delete(RecurringBooking recurringBooking) {
        database.delete(
                TABLE,
                "id = ?",
                new String[]{recurringBooking.getId().toString()}
        );
    }

    private Cursor executeRaw(QueryInterface query) {
        return database.rawQuery(String.format(
                query.sql(),
                query.values()
        ), null);
    }
}
