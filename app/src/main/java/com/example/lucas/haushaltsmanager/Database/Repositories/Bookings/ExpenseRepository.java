package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepositoryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.CannotDeleteExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryTransformer;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepositoryInterface;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.IBooking;
import com.example.lucas.haushaltsmanager.Entities.Expense.ParentBooking;
import com.example.lucas.haushaltsmanager.Entities.Price;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class ExpenseRepository {
    private static final String TAG = ExpenseRepository.class.getSimpleName();
    private static final String TABLE = "BOOKINGS";

    private final SQLiteDatabase mDatabase;
    private final TransformerInterface<ExpenseObject> transformer;
    private final AccountRepositoryInterface accountRepository;

    public ExpenseRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
        transformer = new BookingTransformer(
                new CategoryTransformer()
        );
        accountRepository = new AccountRepository(context);
    }

    public ExpenseObject get(UUID id) throws ExpenseNotFoundException {
        Cursor c = executeRaw(new GetBookingQuery(id));

        if (!c.moveToFirst()) {
            throw ExpenseNotFoundException.expenseNotFoundException(id);
        }

        ExpenseObject expense = transformer.transform(c);

        c.close();
        return expense;
    }

    public List<ExpenseObject> getAll() {
        Cursor c = executeRaw(new GetAllBookingsQuery(
                0,
                Calendar.getInstance().getTimeInMillis()
        ));

        c.moveToFirst();
        ArrayList<ExpenseObject> bookings = new ArrayList<>();
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
        values.put("expense_type", ExpenseObject.EXPENSE_TYPES.PARENT_EXPENSE.name());

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
        for (ExpenseObject child : parentBooking.getChildren()) {
            childRepo.insert(parentBooking, child);
        }
    }

    public void insert(ExpenseObject expense) {
        // TODO: Method could fail with SQLException when referenced Account/Currency/Category is not existing

        ContentValues values = new ContentValues();
        values.put("id", expense.getId().toString());
        values.put("expense_type", expense.getExpenseType().name());
        values.put("price", expense.getUnsignedPrice());
        values.put("category_id", expense.getCategory().getId().toString());
        values.put("expenditure", expense.isExpenditure() ? 1 : 0);
        values.put("title", expense.getTitle());
        values.put("date", expense.getDate().getTimeInMillis());
        values.put("notice", expense.getNotice());
        values.put("account_id", expense.getAccountId().toString());
        values.put("hidden", 0);

        try {
            // TODO: Use Transaction to insert booking and update account balance
            if (expense.getExpenseType() != ExpenseObject.EXPENSE_TYPES.DUMMY_EXPENSE && expense.getExpenseType() != ExpenseObject.EXPENSE_TYPES.DATE_PLACEHOLDER && expense.getExpenseType() != ExpenseObject.EXPENSE_TYPES.PARENT_EXPENSE) {
                updateAccountBalance(
                        expense.getAccountId(),
                        expense.getSignedPrice()
                );
            }
        } catch (AccountNotFoundException e) {

            Log.e(TAG, "Could not find account", e);
            //Sollte eigentlich nicht passieren können da der User nur aus existierenden Konten auswählen kann.
        }

        mDatabase.insertOrThrow(
                TABLE,
                null,
                values
        );

        for (ExpenseObject child : expense.getChildren()) {
            new ChildExpenseRepository(app.getContext()).insert(expense, child);
        }
    }

    public void delete(ExpenseObject expense) throws CannotDeleteExpenseException {
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

            updateAccountBalance(
                    expense.getAccountId(),
                    -expense.getSignedPrice()
            );

            mDatabase.setTransactionSuccessful();
        } catch (AccountNotFoundException e) {

            Log.e(TAG, "Could not delete Booking " + expense.getTitle() + " attached Account " + expense.getAccountId() + " does not exist");
        } finally {
            mDatabase.endTransaction();
        }
    }

    public void update(ExpenseObject expense) throws ExpenseNotFoundException {
        ContentValues updatedExpense = new ContentValues();
        updatedExpense.put("expense_type", expense.getExpenseType().name());
        updatedExpense.put("price", expense.getUnsignedPrice());
        updatedExpense.put("expenditure", expense.isExpenditure() ? 1 : 0);
        updatedExpense.put("title", expense.getTitle());
        updatedExpense.put("date", expense.getDate().getTimeInMillis());
        updatedExpense.put("notice", expense.getNotice());
        updatedExpense.put("category_id", expense.getCategory().getId().toString());
        updatedExpense.put("account_id", expense.getAccountId().toString());

        try {
            ExpenseObject oldExpense = get(expense.getId());

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

        } catch (AccountNotFoundException e) {
            // Do nothing
        }
    }

    public void hide(ExpenseObject expense) throws ExpenseNotFoundException {

        ContentValues values = new ContentValues();
        values.put("hidden", 1);

        int affectedRows = mDatabase.update(
                TABLE,
                values,
                "id = ?",
                new String[]{expense.getId().toString()}
        );

        if (affectedRows == 0) {
            throw ExpenseNotFoundException.expenseNotFoundException(expense.getId());
        }

        if (!expense.isParent()) {
            // TODO: Use Transaction to update booking and update account balance
            try {
                updateAccountBalance(
                        expense.getAccountId(),
                        -expense.getSignedPrice()
                );
            } catch (AccountNotFoundException e) {
                // Do nothing
            }
        }
    }

    public boolean isHidden(ExpenseObject expense) throws ExpenseNotFoundException {
        Cursor c = executeRaw(new IsBookingHiddenQuery(expense));

        if (!c.moveToFirst()) {
            throw ExpenseNotFoundException.expenseNotFoundException(expense.getId());
        }

        boolean isHidden = c.getInt(c.getColumnIndex("hidden")) == 1;
        c.close();

        return isHidden;
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
    private void updateAccountBalance(UUID accountId, double amount) throws AccountNotFoundException {
        Account account = accountRepository.get(accountId);

        double newBalance = account.getBalance().getSignedValue() + amount;
        account.setBalance(new Price(newBalance));
        accountRepository.update(account);
    }

    private boolean hasChildren(ExpenseObject expense) {
        Cursor c = executeRaw(new HasBookingChildrenQuery(expense));

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }
}
