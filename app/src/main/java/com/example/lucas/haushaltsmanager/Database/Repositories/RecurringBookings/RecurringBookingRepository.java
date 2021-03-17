package com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryTransformer;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.CurrencyTransformer;
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
                new CurrencyTransformer(),
                new ChildCategoryTransformer()
        );
    }

    public RecurringBooking create(RecurringBooking recurringBooking) {
        ExpenseObject booking = recurringBooking.getBooking();

        ContentValues values = new ContentValues();
        values.put("expense_type", booking.getExpenseType().name());
        values.put("price", booking.getUnsignedPrice());
        values.put("category_id", booking.getCategory().getIndex());
        values.put("expenditure", booking.getPrice().isNegative() ? 1 : 0);
        values.put("title", booking.getTitle());
        values.put("date", booking.getDate().getTimeInMillis());
        values.put("account_id", booking.getAccountId());
        values.put("currency_id", booking.getCurrency().getIndex());

        values.put("calendar_field", recurringBooking.getFrequency().getCalendarField());
        values.put("amount", recurringBooking.getFrequency().getAmount());
        values.put("start", recurringBooking.getExecutionDate().getTimeInMillis());
        values.put("end", recurringBooking.getEnd().getTimeInMillis());

        long insertedIndex = mDatabase.insert("RECURRING_BOOKINGS", null, values);

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

        if (!c.moveToFirst()) {
            throw new RecurringBookingNotFoundException(index);
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
        ExpenseObject booking = recurringBooking.getBooking();

        ContentValues values = new ContentValues();
        values.put("expense_type", booking.getExpenseType().name());
        values.put("price", booking.getUnsignedPrice());
        values.put("category_id", booking.getCategory().getIndex());
        values.put("expenditure", booking.getPrice().isNegative() ? 1 : 0);
        values.put("title", booking.getTitle());
        values.put("date", booking.getDate().getTimeInMillis());
        values.put("account_id", booking.getAccountId());
        values.put("currency_id", booking.getCurrency().getIndex());

        values.put("calendar_field", recurringBooking.getFrequency().getCalendarField());
        values.put("amount", recurringBooking.getFrequency().getAmount());
        values.put("start", recurringBooking.getExecutionDate().getTimeInMillis());
        values.put("end", recurringBooking.getEnd().getTimeInMillis());

        return 1 == mDatabase.update(
                "RECURRING_BOOKINGS",
                values,
                "id = ?",
                new String[]{"" + recurringBooking.getIndex()}
        );
    }

    public void delete(RecurringBooking recurringBooking) {
        mDatabase.delete(
                ExpensesDbHelper.TABLE_RECURRING_BOOKINGS,
                "id = ?",
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
