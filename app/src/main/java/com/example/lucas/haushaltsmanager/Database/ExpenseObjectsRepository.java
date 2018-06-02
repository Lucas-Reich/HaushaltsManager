package com.example.lucas.haushaltsmanager.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Calendar;

public class ExpenseObjectsRepository {

    private static String TAG = AccountsRepository.class.getSimpleName();

    private ExpensesDbHelper dbHelper;
    private SQLiteDatabase database;
    private Calendar CAL;
    private CurrenciesRepository currenciesRepository;
    private ChildrenRepository childrenRepository;

    public ExpenseObjectsRepository(Context context) {

        dbHelper = new ExpensesDbHelper(context);
        currenciesRepository = new CurrenciesRepository(context);
        childrenRepository = new ChildrenRepository(context);
    }

    public void open() {

        if (!isOpen())
            database = dbHelper.getWritableDatabase();
        Log.d(TAG, "Opened ExpenseObjects repository connection");
    }

    public void close() {

        dbHelper.close();
        Log.d(TAG, "Closed ExpenseObjects repository connection");
    }

    public boolean isOpen() {

        return database != null && database.isOpen();
    }

    /**
     * Convenience Method for mapping a Cursor to a Booking
     *
     * @param c Cursor object obtained by a SQLITE search query
     * @return An remapped ExpenseObject
     *//*
    private ExpenseObject cursorToExpense(Cursor c) {

        CAL = Calendar.getInstance();

        int expenseId = c.getInt(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_ID));

        Log.d(TAG, "cursorToExpense: " + expenseId);

        String date = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_DATE));

        String title = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_TITLE));

        double price = c.getDouble(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_PRICE));

        boolean expenditure = c.getInt(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE)) == 1;

        String notice = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_NOTICE));

        long categoryId = c.getLong(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID));
        String categoryName = c.getString(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_NAME));
        String categoryColor = c.getString(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_COLOR));
        boolean defaultExpenseType = c.getInt(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE)) == 1;
        Category category = new Category(categoryId, categoryName, categoryColor, defaultExpenseType);

        long curId = c.getLong(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID));
        String curName = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_NAME));
        String curShortName = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME));
        String curSymbol = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_SYMBOL));
        Double rateToBase = getRateToBase(curId, date.substring(0, 10));
        Currency currency = new Currency(curId, curName, curShortName, curSymbol, rateToBase);

        long accountId = c.getLong(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID));
        String accountName = c.getString(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_NAME));
        int accountBalance = c.getInt(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_BALANCE));
        Account account = new Account(accountId, accountName, accountBalance, currency);

        int exchangeRate = c.getInt(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_EXCHANGE_RATE));

        long currencyId = c.getLong(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID));
        Currency expenseCurrency = currenciesRepository.getCurrencyByShortName(currencyId);
        expenseCurrency.setRateToBase(exchangeRate);

        ExpenseObject expense = new ExpenseObject(expenseId, title, price, expenditure, date, category, notice, account, expenseCurrency);

        boolean isParent = c.getInt(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_IS_PARENT)) == 1;
        if (isParent) {

            Log.d(TAG, "cursorToExpense: " + expenseId);
            expense.addChildren(getChildrenToParent(expenseId));
        }

        expense.setTags(getTagsToBooking(expense.getIndex()));

        return expense;
    }*/
}
