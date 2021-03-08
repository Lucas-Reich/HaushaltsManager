package com.example.lucas.haushaltsmanager.Database.Repositories.Currencies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.Exceptions.CurrencyNotFoundException;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Currency;

import java.util.ArrayList;
import java.util.List;

public class CurrencyRepository implements CurrencyRepositoryInterface {
    private SQLiteDatabase mDatabase;
    private final TransformerInterface<Currency> transformer;

    public CurrencyRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
        this.transformer = new CurrencyTransformer();
    }

    public Currency get(long currencyId) throws CurrencyNotFoundException {
        Cursor c = executeRaw(new GetCurrencyQuery(currencyId));

        if (!c.moveToFirst()) {
            throw new CurrencyNotFoundException(currencyId);
        }

        return transformer.transform(c);
    }

    public List<Currency> getAll() {
        Cursor c = executeRaw(new GetAllCurrenciesQuery());

        ArrayList<Currency> currencies = new ArrayList<>();
        while (c.moveToNext())
            currencies.add(transformer.transform(c));

        return currencies;
    }

    public Currency insert(Currency currency) {
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

    public void closeDatabase() {
        DatabaseManager.getInstance().closeDatabase();
    }

    private Cursor executeRaw(QueryInterface query) {
        return mDatabase.rawQuery(String.format(
                query.sql(),
                query.values()
        ), null);
    }
}
