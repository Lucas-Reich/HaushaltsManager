package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings;

import android.database.Cursor;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.entities.Booking.Booking;
import com.example.lucas.haushaltsmanager.entities.Booking.IBooking;
import com.example.lucas.haushaltsmanager.entities.Booking.ParentBooking;
import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.entities.Price;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class BookingTransformer implements TransformerInterface<IBooking> {
    private final TransformerInterface<Category> categoryTransformer;

    public BookingTransformer(TransformerInterface<Category> categoryTransformer) {
        this.categoryTransformer = categoryTransformer;
    }

    @Override
    public IBooking transform(Cursor c) {
        UUID id = getId(c);
        String title = c.getString(c.getColumnIndex("title"));
        Calendar date = getDate(c);

        if (expenseType.equals(Booking.EXPENSE_TYPES.PARENT_EXPENSE)) {
            ArrayList<Booking> children = new ChildExpenseRepository(app.getContext()).getAll(id);

            return new ParentBooking(
                    id,
                    date,
                    title,
                    children
            );
        }

        return new Booking(
                id,
                title,
                getPrice(c),
                date,
                categoryTransformer.transform(c),
                getAccountId(c)
        );
    }

    private UUID getId(Cursor c) {
        String rawId = c.getString(c.getColumnIndex("id"));

        return UUID.fromString(rawId);
    }

    private UUID getAccountId(Cursor c) {
        String rawAccountId = c.getString(c.getColumnIndex("account_id"));

        return UUID.fromString(rawAccountId);
    }

    private Price getPrice(Cursor c) {
        double rawPrice = c.getDouble(c.getColumnIndex("price"));
        boolean expenditure = c.getInt(c.getColumnIndex("expenditure")) == 1;

        return new Price(rawPrice, expenditure);
    }

    private Calendar getDate(Cursor c) {
        String dateString = c.getString(c.getColumnIndex("date"));

        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(Long.parseLong(dateString));

        return date;
    }
}
