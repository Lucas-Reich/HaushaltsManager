package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.BookingTags.BookingTagRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.CannotDeleteExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.CannotDeleteChildExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.CurrencyRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings.RecurringBookingRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Templates.TemplateRepository;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Tag;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExpenseRepository {

    public static boolean exists(ExpenseObject expense) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery;

        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ID + " = " + expense.getIndex()
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_TITLE + " = '" + expense.getTitle() + "'"
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_PRICE + " = " + expense.getUnsignedPrice()
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE + " = '" + expense.getExpenseType() + "'"
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID + " = " + expense.getCategory().getIndex()
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID + " = " + expense.getAccountId()
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE + " = " + (expense.isExpenditure() ? 1 : 0)
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_DATE + " = " + expense.getDateTime().getTimeInMillis()
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_IS_PARENT + " = " + (expense.isParent() ? 1 : 0)
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_NOTICE + " = '" + expense.getNotice() + "'"
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID + " = " + expense.getCurrency().getIndex()
                + " LIMIT 1;";

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {

            c.close();
            DatabaseManager.getInstance().closeDatabase();
            return true;
        }

        c.close();
        DatabaseManager.getInstance().closeDatabase();
        return false;
    }

    public static ExpenseObject get(long expenseId) throws ExpenseNotFoundException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery = "SELECT "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ID + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_PRICE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_TITLE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_DATE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_NOTICE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_IS_PARENT + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_ID + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SYMBOL + ", "
                + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + ", "
                + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " LEFT JOIN " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + " ON " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID + " = " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_ID
                + " LEFT JOIN " + ExpensesDbHelper.TABLE_CURRENCIES + " ON " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID + " = " + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_ID
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ID + " = " + expenseId
                + " ORDER BY " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_DATE + " DESC;";

        Cursor c = db.rawQuery(selectQuery, null);

        if (!c.moveToFirst()) {
            throw ExpenseNotFoundException.expenseNotFoundException(expenseId);
        }

        ExpenseObject expense = cursorToExpense(c);

        c.close();
        DatabaseManager.getInstance().closeDatabase();
        return expense;
    }

    public static List<ExpenseObject> getAll() {

        return getAll(0, Calendar.getInstance().getTimeInMillis());
    }

    /**
     * Method for receiving all bookings in a specified date range
     *
     * @param startDateInMills startind date
     * @param endDateInMills   ending date
     * @return list of Expenses which are between the starting and end date
     */
    public static List<ExpenseObject> getAll(long startDateInMills, long endDateInMills) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery;
        selectQuery = "SELECT "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ID + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_PRICE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_TITLE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_DATE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_NOTICE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_IS_PARENT + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_ID + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SYMBOL + ", "
                + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + ", "
                + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " LEFT JOIN " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + " ON " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID + " = " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_ID
                + " LEFT JOIN " + ExpensesDbHelper.TABLE_CURRENCIES + " ON " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID + " = " + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_ID
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_DATE + " BETWEEN " + startDateInMills + " AND " + endDateInMills
                + " ORDER BY " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_DATE + " DESC;";

        Cursor c = db.rawQuery(selectQuery, null);

        c.moveToFirst();
        ArrayList<ExpenseObject> bookings = new ArrayList<>();
        while (!c.isAfterLast()) {

            bookings.add(cursorToExpense(c));
            c.moveToNext();
        }

        c.close();
        DatabaseManager.getInstance().closeDatabase();

        return bookings;
    }

    public static ExpenseObject insert(ExpenseObject expense) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE, expense.getExpenseType().name());
        values.put(ExpensesDbHelper.BOOKINGS_COL_PRICE, expense.getUnsignedPrice());
        values.put(ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID, expense.getCategory().getIndex());
        values.put(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE, expense.isExpenditure());
        values.put(ExpensesDbHelper.BOOKINGS_COL_TITLE, expense.getTitle());
        values.put(ExpensesDbHelper.BOOKINGS_COL_DATE, expense.getDateTime().getTimeInMillis());
        values.put(ExpensesDbHelper.BOOKINGS_COL_NOTICE, expense.getNotice());
        values.put(ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID, expense.getAccountId());
        values.put(ExpensesDbHelper.BOOKINGS_COL_IS_PARENT, expense.isParent());
        values.put(ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID, expense.getCurrency().getIndex());

        try {
            if (expense.getExpenseType() != ExpenseObject.EXPENSE_TYPES.DUMMY_EXPENSE && expense.getExpenseType() != ExpenseObject.EXPENSE_TYPES.DATE_PLACEHOLDER && expense.getExpenseType() != ExpenseObject.EXPENSE_TYPES.PARENT_EXPENSE) {
                updateAccountBalance(
                        expense.getAccountId(),
                        expense.getSignedPrice()
                );
            }
        } catch (AccountNotFoundException e) {
            //Sollte eingentlich nicht passieren können da der User nur aus existierenden Konten auswählen kann.
        }

        long insertedExpenseId = db.insert(ExpensesDbHelper.TABLE_BOOKINGS, null, values);
        DatabaseManager.getInstance().closeDatabase();

        ExpenseObject insertedExpense = new ExpenseObject(
                insertedExpenseId,
                expense.getTitle(),
                expense.getUnsignedPrice(),
                expense.getDateTime(),
                expense.isExpenditure(),
                expense.getCategory(),
                expense.getNotice(),
                expense.getAccountId(),
                expense.getExpenseType(),
                expense.getTags(),
                new ArrayList<ExpenseObject>(),
                expense.getCurrency()
        );

        for (Tag tag : expense.getTags()) {
            BookingTagRepository.insert(insertedExpense.getIndex(), tag, insertedExpense.getExpenseType());//todo kann ich die Tags hier zur buchung zuordnen anstatt durch expense.getTags()
        }

        for (ExpenseObject child : expense.getChildren()) {
            insertedExpense.addChild(ChildExpenseRepository.insert(insertedExpense, child));
        }

        return insertedExpense;
    }

    public static void delete(ExpenseObject expense) throws CannotDeleteExpenseException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        if (isRecurringBooking(expense) || isTemplateBooking(expense)) {
            //todo wenn eine Buchung gelöscht werden soll die noch als Wiederkerhende- oder Template- Buchung hinterlegt ist, muss sie stattdessen als hidden markiert werden
            Toast.makeText(app.getContext(), "Buchung ist eine Vorlagen oder eine Wiederkehrende Buchung", Toast.LENGTH_SHORT).show();
        }

        if (isAttachedToChildExpenses(expense))
            throw CannotDeleteExpenseException.BookingAttachedToChildException(expense);

        try {
            updateAccountBalance(
                    expense.getAccountId(),
                    -expense.getSignedPrice()
            );

            BookingTagRepository.deleteAll(expense);
            for (ExpenseObject childExpense : expense.getChildren()) {
                ChildExpenseRepository.delete(childExpense);
            }
        } catch (AccountNotFoundException e) {

            throw CannotDeleteExpenseException.RelatedAccountDoesNotExist(expense);
        } catch (CannotDeleteChildExpenseException e) {

            throw CannotDeleteExpenseException.CannotDeleteChild(expense);
        }

        db.delete(ExpensesDbHelper.TABLE_BOOKINGS, ExpensesDbHelper.BOOKINGS_COL_ID + " = ?", new String[]{"" + expense.getIndex()});
        DatabaseManager.getInstance().closeDatabase();
    }

    public static void update(ExpenseObject expense) throws ExpenseNotFoundException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ContentValues updatedExpense = new ContentValues();
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE, expense.getExpenseType().name());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_PRICE, expense.getUnsignedPrice());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID, expense.getCategory().getIndex());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE, expense.isExpenditure());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_TITLE, expense.getTitle());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_DATE, expense.getDateTime().getTimeInMillis());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_NOTICE, expense.getNotice());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID, expense.getAccountId());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID, expense.getCurrency().getIndex());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_IS_PARENT, expense.isParent());

        BookingTagRepository.deleteAll(expense);
        for (Tag tag : expense.getTags()) {
            BookingTagRepository.insert(expense.getIndex(), tag, expense.getExpenseType());
        }

        try {
            ExpenseObject oldExpense = get(expense.getIndex());

            updateAccountBalance(
                    expense.getAccountId(),
                    expense.getSignedPrice() - oldExpense.getSignedPrice()
            );

            int affectedRows = db.update(ExpensesDbHelper.TABLE_BOOKINGS, updatedExpense, ExpensesDbHelper.BOOKINGS_COL_ID + " = ?", new String[]{expense.getIndex() + ""});
            DatabaseManager.getInstance().closeDatabase();

            if (affectedRows == 0)
                throw ExpenseNotFoundException.expenseNotFoundException(expense.getIndex());

        } catch (AccountNotFoundException e) {

            DatabaseManager.getInstance().closeDatabase();
        }
    }

    /**
     * Methode um den Kontostand anzupassen.
     *
     * @param accountId Konto welches angepasst werden soll
     * @param amount    Betrag der angezogen oder hinzugefügt werden soll
     */
    private static void updateAccountBalance(long accountId, double amount) throws AccountNotFoundException {

        Account account1 = AccountRepository.get(accountId);
        account1.setBalance(account1.getBalance() + amount);
        AccountRepository.update(account1);
    }

    private static boolean isAttachedToChildExpenses(ExpenseObject expense) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery;
        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_CHILD_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_PARENT_BOOKING_ID + " = " + expense.getIndex()
                + " LIMIT 1;";

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {

            c.close();
            DatabaseManager.getInstance().closeDatabase();
            return true;
        }

        c.close();
        DatabaseManager.getInstance().closeDatabase();
        return false;
    }

    private static boolean isTemplateBooking(ExpenseObject expense) {
        return TemplateRepository.exists(expense);
    }

    private static boolean isRecurringBooking(ExpenseObject expense) {
        return RecurringBookingRepository.exists(expense);
    }

    public static ExpenseObject cursorToExpense(Cursor c) {

        int expenseId = c.getInt(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_ID));
        Calendar date = Calendar.getInstance();
        String dateString = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_DATE));
        date.setTimeInMillis(Long.parseLong(dateString));
        String title = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_TITLE));
        double price = c.getDouble(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_PRICE));
        boolean expenditure = c.getInt(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE)) == 1;
        String notice = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_NOTICE));
        long accountId = c.getLong(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID));
        ExpenseObject.EXPENSE_TYPES expense_type = ExpenseObject.EXPENSE_TYPES.valueOf(c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE)));

        return new ExpenseObject(
                expenseId,
                title,
                price,
                date,
                expenditure,
                ChildCategoryRepository.cursorToChildCategory(c),
                notice,
                accountId,
                expense_type,
                BookingTagRepository.get(expenseId, expense_type),
                expense_type.equals(ExpenseObject.EXPENSE_TYPES.PARENT_EXPENSE) ? ChildExpenseRepository.getAll(expenseId) : new ArrayList<ExpenseObject>(),
                CurrencyRepository.cursorToCurrency(c)
        );
    }

    public static void assertSavableExpense(ExpenseObject expense) {
        switch (expense.getExpenseType()) {
            case PARENT_EXPENSE:
            case NORMAL_EXPENSE:
            case CHILD_EXPENSE:
                break;
            case DATE_PLACEHOLDER:
            case TRANSFER_EXPENSE://todo kann man transfer buchungen wirklich nicht speichern?
            case DUMMY_EXPENSE:
                throw new UnsupportedOperationException("Booking type cannot be saved.");
        }
    }
}
