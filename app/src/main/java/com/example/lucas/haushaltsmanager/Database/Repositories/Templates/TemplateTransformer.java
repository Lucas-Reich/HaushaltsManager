package com.example.lucas.haushaltsmanager.Database.Repositories.Templates;

import android.database.Cursor;

import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.entities.Booking.Booking;
import com.example.lucas.haushaltsmanager.entities.Price;
import com.example.lucas.haushaltsmanager.entities.TemplateBooking;

import java.util.Calendar;
import java.util.UUID;

public class TemplateTransformer implements TransformerInterface<TemplateBooking> {
    private final TransformerInterface<Category> categoryTransformer;

    public TemplateTransformer(
            TransformerInterface<Category> categoryTransformer
    ) {
        this.categoryTransformer = categoryTransformer;
    }

    @Override
    public TemplateBooking transform(Cursor c) {
        return new TemplateBooking(
                getId(c),
                transformExpense(c)
        );
    }

    private UUID getId(Cursor c) {
        String rawId = c.getString(c.getColumnIndex("id"));

        return UUID.fromString(rawId);
    }

    private Booking transformExpense(Cursor c) {
        String title = c.getString(c.getColumnIndex("title"));
        Category category = categoryTransformer.transform(c);

        return new Booking(
                new UUID(0, 0),
                title,
                extractPrice(c),
                extractDate(c),
                category,
                "",
                getAccountId(c),
                extractExpenseType(c)
        );
    }

    private UUID getAccountId(Cursor c) {
        String rawAccountId = c.getString(c.getColumnIndex("account_id"));

        return UUID.fromString(rawAccountId);
    }

    private Booking.EXPENSE_TYPES extractExpenseType(Cursor c) {
        String rawExpenseType = c.getString(c.getColumnIndex("expense_type"));

        return Booking.EXPENSE_TYPES.valueOf(rawExpenseType);
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
}
