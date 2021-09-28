package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.room.Room;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.AppDatabase;
import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.AccountDAO;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.CannotDeleteExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryTransformer;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepositoryInterface;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.entities.Account;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;
import com.example.lucas.haushaltsmanager.entities.booking.IBooking;
import com.example.lucas.haushaltsmanager.entities.booking.ParentBooking;
import com.example.lucas.haushaltsmanager.entities.Price;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class ExpenseRepository {
    private static final String TABLE = "BOOKINGS";

    private final SQLiteDatabase mDatabase;
    private final TransformerInterface<IBooking> transformer;
    private final AccountDAO accountRepo;

    public ExpenseRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
        transformer = new BookingTransformer(
                new CategoryTransformer()
        );
        accountRepo = Room.databaseBuilder(context, AppDatabase.class, "expenses")
                .build().accountDAO();
    }

    public IBooking getNew(UUID id) throws ExpenseNotFoundException {
        Cursor c = executeRaw(new GetBookingQuery(id));

        if (!c.moveToFirst()) {
            throw ExpenseNotFoundException.expenseNotFoundException(id);
        }

        IBooking expense = transformer.transform(c);

        c.close();
        return expense;
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

        values.put("price", parentBooking.getPrice().getUnsignedValue());
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
            childRepo.insert(parentBooking, child);
        }
    }

    public void insert(Booking expense) {
        // TODO: Method could fail with SQLException when referenced Account/Currency/Category is not existing

        ContentValues values = new ContentValues();
        values.put("id", expense.getId().toString());
        values.put("price", expense.getUnsignedPrice());
        values.put("category_id", expense.getCategory().getId().toString());
        values.put("expenditure", expense.isExpenditure() ? 1 : 0);
        values.put("title", expense.getTitle());
        values.put("date", expense.getDate().getTimeInMillis());
        values.put("account_id", expense.getAccountId().toString());
        values.put("hidden", 0);

        // TODO: Use Transaction to insert booking and update account balance
        updateAccountBalance(
                expense.getAccountId(),
                expense.getSignedPrice()
        );

        mDatabase.insertOrThrow(
                TABLE,
                null,
                values
        );
    }

    public void delete(IBooking expense) throws CannotDeleteExpenseException {
        if (hasChildren(expense)) {
            throw CannotDeleteExpenseException.BookingAttachedToChildException(expense);
        }

        try {
            mDatabase.beginTransaction();

            mDatabase.delete(
                    TABLE,
                    "id = ?",
                    new String[]{expense.getId().toString()}
            );

            if (expense instanceof Booking) {
                updateAccountBalance(
                        ((Booking) expense).getAccountId(),
                        -((Booking) expense).getSignedPrice()
                );
            }

            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }
    }

    public void update(Booking expense) throws ExpenseNotFoundException {
        ContentValues updatedExpense = new ContentValues();
        updatedExpense.put("price", expense.getUnsignedPrice());
        updatedExpense.put("expenditure", expense.isExpenditure() ? 1 : 0);
        updatedExpense.put("title", expense.getTitle());
        updatedExpense.put("date", expense.getDate().getTimeInMillis());
        updatedExpense.put("category_id", expense.getCategory().getId().toString());
        updatedExpense.put("account_id", expense.getAccountId().toString());

        Booking oldExpense = (Booking) getNew(expense.getId());

        updateAccountBalance(
                expense.getAccountId(),
                expense.getSignedPrice() - oldExpense.getSignedPrice()
        );

        int affectedRows = mDatabase.update(
                TABLE,
                updatedExpense,
                "id = ?",
                new String[]{expense.getId().toString()}
        );


        if (affectedRows == 0) {
            throw ExpenseNotFoundException.couldNotUpdateReferencedExpense(expense.getId());
        }
    }

    private Cursor executeRaw(QueryInterface query) {
        return mDatabase.rawQuery(String.format(
                query.sql(),
                query.values()
        ), null);
    }

    /**
     * Methode um den Kontostand anzupassen.
     *
     * @param accountId Konto welches angepasst werden soll
     * @param amount    Betrag der angezogen oder hinzugefügt werden soll
     */
    private void updateAccountBalance(UUID accountId, double amount) {
        Account account = accountRepo.get(accountId);

        double newBalance = account.getPrice().getSignedValue() + amount;
        account.setPrice(new Price(newBalance));
        accountRepo.update(account);
    }

    private boolean hasChildren(IBooking booking) {
        Cursor c = executeRaw(new HasBookingChildrenQuery(booking));

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }
}
