package com.example.lucas.haushaltsmanager.Database.Repositories.Currencies;

import android.content.ContentValues;
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

    public static boolean exists(Currency currency) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery;

        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_CURRENCIES
                + " WHERE " + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_ID + " = " + currency.getIndex()
                + " AND " + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_NAME + " = '" + currency.getName() + "'"
                + " AND " + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + " = '" + currency.getShortName() + "'"
                + " AND " + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SYMBOL + " = '" + currency.getSymbol() + "'"
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

    public static Currency get(long currencyId) throws CurrencyNotFoundException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CURRENCIES_COL_ID + ", "
                + ExpensesDbHelper.CURRENCIES_COL_NAME + ", "
                + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + ", "
                + ExpensesDbHelper.CURRENCIES_COL_SYMBOL
                + " FROM " + ExpensesDbHelper.TABLE_CURRENCIES
                + " WHERE " + ExpensesDbHelper.CURRENCIES_COL_ID + " = " + currencyId + ";";

        Cursor c = db.rawQuery(selectQuery, null);

        if (!c.moveToFirst()) {
            throw new CurrencyNotFoundException(currencyId);
        }

        Currency currency = cursorToCurrency(c);

        c.close();
        DatabaseManager.getInstance().closeDatabase();
        return currency;
    }

    public static Currency getByShortName(String currencyName) throws CurrencyNotFoundException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CURRENCIES_COL_ID + ", "
                + ExpensesDbHelper.CURRENCIES_COL_NAME + ", "
                + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + ", "
                + ExpensesDbHelper.CURRENCIES_COL_SYMBOL
                + " FROM " + ExpensesDbHelper.TABLE_CURRENCIES
                + " WHERE " + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + " = '" + currencyName + "';";

        Cursor c = db.rawQuery(selectQuery, null);

        if (!c.moveToFirst()) {
            throw new CurrencyNotFoundException(currencyName);
        }

        Currency currency = cursorToCurrency(c);

        c.close();
        DatabaseManager.getInstance().closeDatabase();

        return currency;
    } //todo ersetze die Funktion durch get()

    public static List<Currency> getAll() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CURRENCIES_COL_ID + ", "
                + ExpensesDbHelper.CURRENCIES_COL_NAME + ", "
                + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + ", "
                + ExpensesDbHelper.CURRENCIES_COL_SYMBOL
                + " FROM " + ExpensesDbHelper.TABLE_CURRENCIES + ";";

        Cursor c = db.rawQuery(selectQuery, null);
        c.moveToFirst();

        ArrayList<Currency> currencies = new ArrayList<>();
        while (!c.isAfterLast()) {

            currencies.add(cursorToCurrency(c));
            c.moveToNext();
        }

        c.close();
        DatabaseManager.getInstance().closeDatabase();
        return currencies;
    }

    public static Currency insert(Currency currency) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.CURRENCIES_COL_NAME, currency.getName());
        values.put(ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME, currency.getShortName());
        values.put(ExpensesDbHelper.CURRENCIES_COL_SYMBOL, currency.getSymbol());

        long insertedCurrencyId = db.insert(ExpensesDbHelper.TABLE_CURRENCIES, null, values);
        DatabaseManager.getInstance().closeDatabase();

        return new Currency(
                insertedCurrencyId,
                currency.getName(),
                currency.getShortName(),
                currency.getSymbol()
        );
    }

    public static void delete(Currency currency) throws CannotDeleteCurrencyException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        if (isAttachedToAccount(currency))
            throw new CannotDeleteCurrencyException(currency);

        db.delete(ExpensesDbHelper.TABLE_CURRENCIES, ExpensesDbHelper.CURRENCIES_COL_ID + " = ?", new String[]{"" + currency.getIndex()});
        DatabaseManager.getInstance().closeDatabase();
    }

    public static void update(Currency currency) throws CurrencyNotFoundException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ContentValues updatedCurrency = new ContentValues();
        updatedCurrency.put(ExpensesDbHelper.CURRENCIES_COL_SYMBOL, currency.getSymbol());
        updatedCurrency.put(ExpensesDbHelper.CURRENCIES_COL_NAME, currency.getName());
        updatedCurrency.put(ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME, currency.getShortName());

        int affectedRows = db.update(
                ExpensesDbHelper.TABLE_CURRENCIES,
                updatedCurrency, ExpensesDbHelper.CURRENCIES_COL_ID + " = ?",
                new String[]{currency.getIndex() + ""}
        );
        DatabaseManager.getInstance().closeDatabase();

        if (affectedRows == 0)
            throw new CurrencyNotFoundException(currency.getIndex());
    }

    private static boolean isAttachedToAccount(Currency currency) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery;
        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_ACCOUNTS
                + " WHERE " + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID + " = " + currency.getIndex()
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
