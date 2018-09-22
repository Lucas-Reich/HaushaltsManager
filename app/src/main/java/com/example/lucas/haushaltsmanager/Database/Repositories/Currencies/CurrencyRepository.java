package com.example.lucas.haushaltsmanager.Database.Repositories.Currencies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.Exceptions.CannotDeleteCurrencyException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.Exceptions.CurrencyNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Currency;

import java.util.ArrayList;
import java.util.List;

public class CurrencyRepository {
    private SQLiteDatabase mDatabase;

    public CurrencyRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
    }

    public boolean exists(Currency currency) {
        String selectQuery;

        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_CURRENCIES
                + " WHERE " + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_ID + " = " + currency.getIndex()
                + " AND " + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_NAME + " = '" + currency.getName() + "'"
                + " AND " + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + " = '" + currency.getShortName() + "'"
                + " AND " + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SYMBOL + " = '" + currency.getSymbol() + "'"
                + " LIMIT 1;";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    public Currency get(long currencyId) throws CurrencyNotFoundException {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CURRENCIES_COL_ID + ", "
                + ExpensesDbHelper.CURRENCIES_COL_NAME + ", "
                + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + ", "
                + ExpensesDbHelper.CURRENCIES_COL_SYMBOL
                + " FROM " + ExpensesDbHelper.TABLE_CURRENCIES
                + " WHERE " + ExpensesDbHelper.CURRENCIES_COL_ID + " = " + currencyId + ";";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (!c.moveToFirst()) {
            throw new CurrencyNotFoundException(currencyId);
        }

        Currency currency = cursorToCurrency(c);

        c.close();
        return currency;
    }

    public List<Currency> getAll() {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CURRENCIES_COL_ID + ", "
                + ExpensesDbHelper.CURRENCIES_COL_NAME + ", "
                + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + ", "
                + ExpensesDbHelper.CURRENCIES_COL_SYMBOL
                + " FROM " + ExpensesDbHelper.TABLE_CURRENCIES + ";";

        Cursor c = mDatabase.rawQuery(selectQuery, null);
        c.moveToFirst();

        ArrayList<Currency> currencies = new ArrayList<>();
        while (!c.isAfterLast()) {

            currencies.add(cursorToCurrency(c));
            c.moveToNext();
        }

        c.close();
        return currencies;
    }

    public Currency insert(Currency currency) {
        SQLiteDatabase mDatabase = DatabaseManager.getInstance().openDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.CURRENCIES_COL_NAME, currency.getName());
        values.put(ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME, currency.getShortName());
        values.put(ExpensesDbHelper.CURRENCIES_COL_SYMBOL, currency.getSymbol());

        long insertedCurrencyId = mDatabase.insert(ExpensesDbHelper.TABLE_CURRENCIES, null, values);

        return new Currency(
                insertedCurrencyId,
                currency.getName(),
                currency.getShortName(),
                currency.getSymbol()
        );
    }

    public void delete(Currency currency) throws CannotDeleteCurrencyException {

        if (isAttachedToAccount(currency))
            throw new CannotDeleteCurrencyException(currency);

        mDatabase.delete(ExpensesDbHelper.TABLE_CURRENCIES, ExpensesDbHelper.CURRENCIES_COL_ID + " = ?", new String[]{"" + currency.getIndex()});
    }

    public void update(Currency currency) throws CurrencyNotFoundException {

        ContentValues updatedCurrency = new ContentValues();
        updatedCurrency.put(ExpensesDbHelper.CURRENCIES_COL_SYMBOL, currency.getSymbol());
        updatedCurrency.put(ExpensesDbHelper.CURRENCIES_COL_NAME, currency.getName());
        updatedCurrency.put(ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME, currency.getShortName());

        int affectedRows = mDatabase.update(
                ExpensesDbHelper.TABLE_CURRENCIES,
                updatedCurrency, ExpensesDbHelper.CURRENCIES_COL_ID + " = ?",
                new String[]{currency.getIndex() + ""}
        );

        if (affectedRows == 0)
            throw new CurrencyNotFoundException(currency.getIndex());
    }

    private boolean isAttachedToAccount(Currency currency) {

        String selectQuery;
        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_ACCOUNTS
                + " WHERE " + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID + " = " + currency.getIndex()
                + " LIMIT 1;";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    public static Currency cursorToCurrency(Cursor c) {
        long currencyId = c.getLong(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_ID));
        String currencyName = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_NAME));
        String currencyShortName = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME));
        String currencySymbol = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_SYMBOL));

        return new Currency(
                currencyId,
                currencyName,
                currencyShortName,
                currencySymbol
        );
    }
}
