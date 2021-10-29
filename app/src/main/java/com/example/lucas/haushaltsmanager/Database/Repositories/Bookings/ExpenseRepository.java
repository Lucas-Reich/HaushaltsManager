package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.CannotDeleteExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepositoryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.AddChildToChildException;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;
import com.example.lucas.haushaltsmanager.entities.booking.IBooking;
import com.example.lucas.haushaltsmanager.entities.booking.ParentBooking;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class ExpenseRepository {
    private static final String TABLE = "BOOKINGS";

    private final SQLiteDatabase mDatabase;
    private final TransformerInterface<IBooking> transformer;

    public ExpenseRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
        transformer = new BookingTransformer();
    }

    public List<IBooking> getAll() {
        Cursor c = executeRaw(new GetAllBookingsQuery(
                0,
                Calendar.getInstance().getTimeInMillis()
        ));

        c.moveToFirst();
        ArrayList<IBooking> bookings = new ArrayList<>();
        while (!c.isAfterLast()) {

            bookings.add(transformer.transform(c));
            c.moveToNext();
        }

        c.close();

        return bookings;
    }

    public void insert(ParentBooking parentBooking) {
        ContentValues values = new ContentValues();
        values.put("id", parentBooking.getId().toString());

        values.put("price", parentBooking.getPrice().getAbsoluteValue());
        values.put("expenditure", parentBooking.getPrice().isNegative() ? 1 : 0);
        values.put("category_id", app.unassignedCategoryId.toString());
        values.put("account_id", new UUID(0, 0).toString());

        values.put("title", parentBooking.getTitle());
        values.put("date", parentBooking.getDate().getTimeInMillis());
        values.put("hidden", 0);

        mDatabase.insertOrThrow(
                TABLE,
                null,
                values
        );

        ChildExpenseRepositoryInterface childRepo = new ChildExpenseRepository(app.getContext());
        for (Booking child : parentBooking.getChildren()) {
            try {
                childRepo.addChildToBooking(child, parentBooking);
            } catch (AddChildToChildException e) {
                // This should never happen
            }
        }
    }

    public void delete(ParentBooking expense) throws CannotDeleteExpenseException {
        if (hasChildren(expense)) {
            throw CannotDeleteExpenseException.bookingAttachedToChildException(expense);
        }

        mDatabase.delete(
                TABLE,
                "id = ?",
                new String[]{expense.getId().toString()}
        );
    }

    private Cursor executeRaw(QueryInterface query) {
        return mDatabase.rawQuery(String.format(
                query.sql(),
                query.values()
        ), null);
    }

    private boolean hasChildren(ParentBooking booking) {
        Cursor c = executeRaw(new HasBookingChildrenQuery(booking));

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }
}
