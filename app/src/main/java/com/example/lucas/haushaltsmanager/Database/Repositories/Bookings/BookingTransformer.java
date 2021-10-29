package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings;

import android.database.Cursor;

import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.entities.Price;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;
import com.example.lucas.haushaltsmanager.entities.booking.IBooking;

import java.util.Calendar;
import java.util.UUID;

public class BookingTransformer implements TransformerInterface<IBooking> {
    @Override
    public IBooking transform(Cursor c) {
        UUID id = getId(c);
        String title = c.getString(c.getColumnIndex("title"));
        Calendar date = getDate(c);

        return new Booking(
                id,
                title,
                getPrice(c),
                date,
                getCategoryId(c),
                getAccountId(c)
        );
    }

    private UUID getCategoryId(Cursor c) {
        String rawCategoryId = c.getString(c.getColumnIndex("category_id"));

        return UUID.fromString(rawCategoryId);
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

        return new Price(rawPrice);
    }

    private Calendar getDate(Cursor c) {
        String dateString = c.getString(c.getColumnIndex("date"));

        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(Long.parseLong(dateString));

        return date;
    }
}
