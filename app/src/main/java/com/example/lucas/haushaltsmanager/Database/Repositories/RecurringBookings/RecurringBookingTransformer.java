package com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings;

import android.database.Cursor;

import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Frequency;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.Entities.RecurringBooking;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class RecurringBookingTransformer implements TransformerInterface<RecurringBooking> {
    private final TransformerInterface<Category> categoryTransformer;

    public RecurringBookingTransformer(
            TransformerInterface<Category> categoryTransformer
    ) {
        this.categoryTransformer = categoryTransformer;
    }

    @Override
    public RecurringBooking transform(Cursor c) {
        if (c.isAfterLast()) {
            return null;
        }

        return new RecurringBooking(
                getId(c),
                getStart(c),
                getEnd(c),
                extractFrequency(c),
                extractExpense(c)
        );
    }

    private UUID getId(Cursor c) {
        String rawId = c.getString(c.getColumnIndex("id"));

        return UUID.fromString(rawId);
    }

    private Calendar getStart(Cursor c) {
        long rawStart = c.getLong(c.getColumnIndex("start"));

        return createFromMillis(rawStart);
    }

    private Calendar getEnd(Cursor c) {
        long rawEnd = c.getLong(c.getColumnIndex("end"));

        return createFromMillis(rawEnd);
    }

    private ExpenseObject extractExpense(Cursor c) {
        String title = c.getString(c.getColumnIndex("title"));
        Category category = categoryTransformer.transform(c);

        return new ExpenseObject(
                new UUID(0, 0),
                title,
                extractPrice(c),
                extractDate(c),
                category,
                "",
                getAccountId(c),
                extractExpenseType(c),
                new ArrayList<ExpenseObject>()
        );
    }

    private UUID getAccountId(Cursor c) {
        String rawAccountId = c.getString(c.getColumnIndex("account_id"));

        return UUID.fromString(rawAccountId);
    }

    private ExpenseObject.EXPENSE_TYPES extractExpenseType(Cursor c) {
        String rawExpenseType = c.getString(c.getColumnIndex("expense_type"));

        return ExpenseObject.EXPENSE_TYPES.valueOf(rawExpenseType);
    }

    private Calendar extractDate(Cursor c) {
        Calendar date = Calendar.getInstance();
        String dateString = c.getString(c.getColumnIndex("date"));
        date.setTimeInMillis(Long.parseLong(dateString));

        return date;
    }

    private Price extractPrice(Cursor c) {
        double rawPrice = c.getDouble(c.getColumnIndex("price"));
        boolean expenditure = c.getInt(c.getColumnIndex("expenditure")) == 1;

        return new Price(rawPrice, expenditure);

    }

    private Frequency extractFrequency(Cursor c) {
        int calendarField = c.getInt(c.getColumnIndex("calendar_field"));
        int amount = c.getInt(c.getColumnIndex("amount"));

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
