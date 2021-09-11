package com.example.lucas.haushaltsmanager.Database.Repositories.Templates;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryTransformer;
import com.example.lucas.haushaltsmanager.Database.Repositories.Templates.Exceptions.TemplateCouldNotBeCreatedException;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Booking.Booking;
import com.example.lucas.haushaltsmanager.Entities.TemplateBooking;

import java.util.ArrayList;
import java.util.List;

public class TemplateRepository {
    private static final String TABLE = "TEMPLATE_BOOKINGS";

    private SQLiteDatabase mDatabase;
    private final TransformerInterface<TemplateBooking> transformer;

    public TemplateRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
        transformer = new TemplateTransformer(
                new CategoryTransformer()
        );
    }

    public List<TemplateBooking> getAll() {
        Cursor c = executeRaw(new GetAllTemplateBookingsQuery());
        c.moveToFirst();

        ArrayList<TemplateBooking> templateBookingBookings = new ArrayList<>();
        while (!c.isAfterLast()) {
            templateBookingBookings.add(transformer.transform(c));
            c.moveToNext();
        }

        c.close();
        return templateBookingBookings;
    }

    public void insert(TemplateBooking templateBooking) throws TemplateCouldNotBeCreatedException {
        Booking expense = templateBooking.getTemplate();

        ContentValues values = new ContentValues();
        values.put("id", templateBooking.getId().toString());
        values.put("expense_type", expense.getExpenseType().name());
        values.put("price", expense.getUnsignedPrice());
        values.put("category_id", expense.getCategory().getId().toString());
        values.put("expenditure", expense.isExpenditure());
        values.put("title", expense.getTitle());
        values.put("date", expense.getDate().getTimeInMillis());
        values.put("account_id", expense.getAccountId().toString());

        try {
            mDatabase.insertOrThrow(
                    TABLE,
                    null,
                    values
            );
        } catch (SQLException e) {
            throw new TemplateCouldNotBeCreatedException(templateBooking, e);
        }
    }

    private Cursor executeRaw(QueryInterface query) {
        return mDatabase.rawQuery(String.format(
                query.sql(),
                query.values()
        ), null);
    }
}
