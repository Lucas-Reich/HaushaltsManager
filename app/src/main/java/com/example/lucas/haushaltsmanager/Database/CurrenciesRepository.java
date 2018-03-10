package com.example.lucas.haushaltsmanager.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Entities.Currency;

import java.util.ArrayList;

public class CurrenciesRepository {

    private static String TAG = AccountsRepository.class.getSimpleName();

    private Context mContext;
    private ExpensesDbHelper dbHelper;
    private SQLiteDatabase database;

    public CurrenciesRepository(Context context) {

        mContext = context;
        dbHelper = new ExpensesDbHelper(mContext);
    }

    public void open() {

        if (!isOpen())
            database = dbHelper.getWritableDatabase();
        Log.d(TAG, "Opened Currencies repository connection");
    }

    public void close() {

        dbHelper.close();
        Log.d(TAG, "Closed Currencies repository connection");
    }

    public boolean isOpen() {

        return database != null && database.isOpen();
    }


    /**
     * Method for mapping an Cursor to a Currency object
     *
     * @param c mDatabase cursor
     * @return Currency object
     */
    @NonNull
    private Currency cursorToCurrency(Cursor c) {

        long currencyId = c.getLong(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_ID));
        String currencyName = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_NAME));
        String currencyShortName = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME));
        String currencySymbol = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_SYMBOL));

        return new Currency(currencyId, currencyName, currencyShortName, currencySymbol);
    }

    public long createCurrency(Currency currency) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.CURRENCIES_COL_NAME, currency.getCurrencyName());
        values.put(ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME, currency.getCurrencyShortName());
        values.put(ExpensesDbHelper.CURRENCIES_COL_SYMBOL, currency.getCurrencySymbol());

        return database.insert(ExpensesDbHelper.TABLE_CURRENCIES, null, values);
    }

    @NonNull
    public Currency getCurrency(long currencyId) {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CURRENCIES_COL_ID + ", "
                + ExpensesDbHelper.CURRENCIES_COL_NAME + ", "
                + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + ", "
                + ExpensesDbHelper.CURRENCIES_COL_SYMBOL
                + " FROM " + ExpensesDbHelper.TABLE_CURRENCIES
                + " WHERE " + ExpensesDbHelper.CURRENCIES_COL_ID + " = " + currencyId + ";";
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getExpenseCurrency: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        return cursorToCurrency(c);
    }

    @Nullable
    public Currency getCurrency(String shortName) {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CURRENCIES_COL_ID + ", "
                + ExpensesDbHelper.CURRENCIES_COL_NAME + ", "
                + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + ", "
                + ExpensesDbHelper.CURRENCIES_COL_SYMBOL
                + " FROM " + ExpensesDbHelper.TABLE_CURRENCIES
                + " WHERE " + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + " = '" + shortName + "';";
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getExpenseCurrency: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        return c.isAfterLast() ? null : cursorToCurrency(c);
    }

    public ArrayList<Currency> getAllCurrencies() {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CURRENCIES_COL_ID + ", "
                + ExpensesDbHelper.CURRENCIES_COL_NAME + ", "
                + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + ", "
                + ExpensesDbHelper.CURRENCIES_COL_SYMBOL
                + " FROM " + ExpensesDbHelper.TABLE_CURRENCIES + ";";
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getAllCurrencies: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        ArrayList<Currency> currencies = new ArrayList<>();
        while (!c.isAfterLast()) {

            currencies.add(cursorToCurrency(c));
            c.moveToNext();
        }

        return currencies;
    }

    public Long getCurrencyId(String curShortName) {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CURRENCIES_COL_ID
                + " FROM " + ExpensesDbHelper.TABLE_CURRENCIES
                + " WHERE " + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + " = '" + curShortName + "';";
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getCurrencyId: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        long currencyId = c.getLong(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_ID));
        c.close();

        return currencyId;
    }

    public long updateCurrency(long index) {

        throw new UnsupportedOperationException("Updating Currencies is not Supported");//todo
    }

    public int deleteCurrency(long index) {

        Log.d(TAG, "deleteCurrency at index: " + index);
        return database.delete(ExpensesDbHelper.TABLE_CURRENCIES, ExpensesDbHelper.CURRENCIES_COL_ID + " = ?", new String[]{"" + index});
    }
}
