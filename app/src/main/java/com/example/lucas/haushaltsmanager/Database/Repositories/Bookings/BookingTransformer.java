package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings;

import android.database.Cursor;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Booking.Booking;
import com.example.lucas.haushaltsmanager.Entities.Booking.IBooking;
import com.example.lucas.haushaltsmanager.Entities.Booking.ParentBooking;
import com.example.lucas.haushaltsmanager.Entities.Price;

import java.util.Calendar;
import java.util.List;
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
        Booking.EXPENSE_TYPES expenseType = getExpenseType(c);

        if (expenseType.equals(Booking.EXPENSE_TYPES.PARENT_EXPENSE)) {
            List<Booking> children = new ChildExpenseRepository(app.getContext()).getAll(id);

            return new ParentBooking(
                    id,
                    title,
                    date,
                    children
            );
        }

        return new Booking(
                id,
                title,
                getPrice(c),
                date,
                categoryTransformer.transform(c),
                c.getString(c.getColumnIndex("notice")),
                getAccountId(c),
                expenseType
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

    private Booking.EXPENSE_TYPES getExpenseType(Cursor c) {
        String rawExpenseType = c.getString(c.getColumnIndex("expense_type"));

        return Booking.EXPENSE_TYPES.valueOf(rawExpenseType);
    }
}
