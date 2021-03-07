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
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.CannotDeleteExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryTransformer;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.CannotDeleteChildExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.CurrencyTransformer;
import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.RecurringBookingRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Templates.TemplateRepository;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExpenseRepository {
    private static final String TAG = ExpenseRepository.class.getSimpleName();
    private final SQLiteDatabase mDatabase;
    private final TransformerInterface<ExpenseObject> transformer;

    public ExpenseRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
        transformer = new BookingTransformer(
                new CurrencyTransformer(),
                new ChildCategoryTransformer(),
                new ChildExpenseRepository(context)
        );
    }

    // TODO: This method is only used within tests
    public boolean exists(ExpenseObject expense) {
        QueryInterface query = new BookingExistsQuery(expense);
        Cursor c = executeRaw(query);

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    public ExpenseObject get(long expenseId) throws ExpenseNotFoundException {
        Cursor c = executeRaw(new GetBookingQuery(expenseId));

        if (!c.moveToFirst()) {
            throw ExpenseNotFoundException.expenseNotFoundException(expenseId);
        }

        ExpenseObject expense = transformer.transform(c);

        c.close();
        return expense;
    }

    public List<ExpenseObject> getAll() {

        return getAll(0, Calendar.getInstance().getTimeInMillis());
    }

    public List<ExpenseObject> getAll(long startDateInMills, long endDateInMills) {
        Cursor c = executeRaw(new GetAllBookingsQuery(startDateInMills, endDateInMills));

        c.moveToFirst();
        ArrayList<ExpenseObject> bookings = new ArrayList<>();
        while (!c.isAfterLast()) {

            bookings.add(transformer.transform(c));
            c.moveToNext();
        }

        c.close();

        return bookings;
    }

    public ExpenseObject insert(ExpenseObject expense) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE, expense.getExpenseType().name());
        values.put(ExpensesDbHelper.BOOKINGS_COL_PRICE, expense.getUnsignedPrice());
        values.put(ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID, expense.getCategory().getIndex());
        values.put(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE, expense.isExpenditure());
        values.put(ExpensesDbHelper.BOOKINGS_COL_TITLE, expense.getTitle());
        values.put(ExpensesDbHelper.BOOKINGS_COL_DATE, expense.getDate().getTimeInMillis());
        values.put(ExpensesDbHelper.BOOKINGS_COL_NOTICE, expense.getNotice());
        values.put(ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID, expense.getAccountId());
        values.put(ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID, expense.getCurrency().getIndex());
        values.put(ExpensesDbHelper.BOOKINGS_COL_HIDDEN, 0);

        try {
            if (expense.getExpenseType() != ExpenseObject.EXPENSE_TYPES.DUMMY_EXPENSE && expense.getExpenseType() != ExpenseObject.EXPENSE_TYPES.DATE_PLACEHOLDER && expense.getExpenseType() != ExpenseObject.EXPENSE_TYPES.PARENT_EXPENSE) {
                updateAccountBalance(
                        expense.getAccountId(),
                        expense.getSignedPrice()
                );
            }
        } catch (AccountNotFoundException e) {

            Log.e(TAG, "", e);
            //Sollte eigentlich nicht passieren können da der User nur aus existierenden Konten auswählen kann.
        }

        long insertedExpenseId = mDatabase.insert(ExpensesDbHelper.TABLE_BOOKINGS, null, values);

        ExpenseObject insertedExpense = new ExpenseObject(
                insertedExpenseId,
                expense.getTitle(),
                expense.getPrice(),
                expense.getDate(),
                expense.getCategory(),
                expense.getNotice(),
                expense.getAccountId(),
                expense.getExpenseType(),
                new ArrayList<ExpenseObject>(),
                expense.getCurrency()
        );

        for (ExpenseObject child : expense.getChildren()) {
            insertedExpense.addChild(new ChildExpenseRepository(app.getContext()).insert(insertedExpense, child));
        }

        return insertedExpense;
    }

    public void delete(ExpenseObject expense) throws CannotDeleteExpenseException {

        if (isRecurringBooking(expense) || isTemplateBooking(expense)) {

            try {

                if (hasChildren(expense)) {
                    throw CannotDeleteExpenseException.BookingAttachedToChildException(expense);
                }

                hide(expense);
            } catch (ExpenseNotFoundException e) {

                // TODO: Was soll passieren, wenn eine Buchung nicht gefunden werden kann, die als TemplateBuchung hinterlegt ist?
                // --> eintrag aus der template tabelle löschen
                // -->
                Log.e(TAG, "Could not find Booking " + expense);
            }
        } else {

            if (hasChildren(expense))
                throw CannotDeleteExpenseException.BookingAttachedToChildException(expense);

            try {
                updateAccountBalance(
                        expense.getAccountId(),
                        -expense.getSignedPrice()
                );

                for (ExpenseObject childExpense : expense.getChildren()) {
                    new ChildExpenseRepository(app.getContext()).delete(childExpense);
                }
            } catch (AccountNotFoundException e) {

                //sollte das Konto aus irgendeinem Grund nicht mehr existieren, muss der Kontostand auch nicht mehr angepasst werden
                Log.e(TAG, "Could not delete Booking " + expense.getTitle() + " attached Account " + expense.getAccountId() + " does not exist");
            } catch (CannotDeleteChildExpenseException e) {

                Log.e(TAG, e.getMessage());
                throw CannotDeleteExpenseException.CannotDeleteChild(expense);
            }

            mDatabase.delete(ExpensesDbHelper.TABLE_BOOKINGS, ExpensesDbHelper.BOOKINGS_COL_ID + " = ?", new String[]{"" + expense.getIndex()});
        }
    }

    public void update(ExpenseObject expense) throws ExpenseNotFoundException {

        ContentValues updatedExpense = new ContentValues();
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE, expense.getExpenseType().name());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_PRICE, expense.getUnsignedPrice());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID, expense.getCategory().getIndex());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE, expense.isExpenditure());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_TITLE, expense.getTitle());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_DATE, expense.getDate().getTimeInMillis());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_NOTICE, expense.getNotice());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID, expense.getAccountId());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID, expense.getCurrency().getIndex());

        try {
            ExpenseObject oldExpense = get(expense.getIndex());

            updateAccountBalance(
                    expense.getAccountId(),
                    expense.getSignedPrice() - oldExpense.getSignedPrice()
            );

            int affectedRows = mDatabase.update(ExpensesDbHelper.TABLE_BOOKINGS, updatedExpense, ExpensesDbHelper.BOOKINGS_COL_ID + " = ?", new String[]{expense.getIndex() + ""});

            if (affectedRows == 0)
                throw ExpenseNotFoundException.expenseNotFoundException(expense.getIndex());

        } catch (AccountNotFoundException e) {
            // Do nothing
        }
    }

    public void hide(ExpenseObject expense) throws ExpenseNotFoundException {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.BOOKINGS_COL_HIDDEN, 1);

        int affectedRows = mDatabase.update(ExpensesDbHelper.TABLE_BOOKINGS, values, ExpensesDbHelper.BOOKINGS_COL_ID + " = ?", new String[]{"" + expense.getIndex()});

        if (affectedRows == 0)
            throw ExpenseNotFoundException.expenseNotFoundException(expense.getIndex());

        if (!expense.isParent()) {
            try {
                updateAccountBalance(
                        expense.getAccountId(),
                        -expense.getSignedPrice()
                );
            } catch (AccountNotFoundException e) {

                // TODO: Die gesamte Transaktion muss zurückgenommen werden und eine CannotDeleteExpenseException muss ausgelösct werden
            }
        }
    }

    public boolean isHidden(ExpenseObject expense) throws ExpenseNotFoundException {
        Cursor c = executeRaw(new IsBookingHiddenQuery(expense));

        if (!c.moveToFirst()) {
            throw ExpenseNotFoundException.expenseNotFoundException(expense.getIndex());
        }

        boolean isHidden = c.getInt(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_HIDDEN)) == 1;
        c.close();

        return isHidden;
    }

    public boolean isTemplateBooking(ExpenseObject expense) {
        return new TemplateRepository(app.getContext()).existsWithoutIndex(expense);// IMPROVEMENT: Das TemplateRepository sollte injected werden.
    }

    public boolean isRecurringBooking(ExpenseObject expense) {
        return new RecurringBookingRepository(app.getContext()).exists(expense);// IMPROVEMENT: Das RecurringBookingRepository sollte injected werden.
    }

    @Deprecated
    public void assertSavableExpense(ExpenseObject expense) {
        switch (expense.getExpenseType()) {
            case PARENT_EXPENSE:
            case NORMAL_EXPENSE:
            case CHILD_EXPENSE:
                break;
            case DATE_PLACEHOLDER:
            case TRANSFER_EXPENSE:
            case DUMMY_EXPENSE:
                throw new UnsupportedOperationException("Booking type cannot be saved.");
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
    private void updateAccountBalance(long accountId, double amount) throws AccountNotFoundException {
        AccountRepository accountRepo = new AccountRepository(app.getContext()); // IMPROVEMENT: Das AccountRepository sollte injected werden.
        Account account = accountRepo.get(accountId);

        double newBalance = account.getBalance().getSignedValue() + amount;
        account.setBalance(new Price(newBalance, account.getBalance().getCurrency()));
        accountRepo.update(account);
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
