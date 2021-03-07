package com.example.lucas.haushaltsmanager.Database.Repositories.Currencies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.BaseRepository;
import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.AccountRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.Exceptions.CannotDeleteCurrencyException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.Exceptions.CurrencyNotFoundException;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Currency;

import java.util.ArrayList;
import java.util.List;

public class CurrencyRepository implements BaseRepository<Currency> {
    private SQLiteDatabase mDatabase;
    private AccountRepository mAccountRepo;
    private final TransformerInterface<Currency> transformer;

    public CurrencyRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
        mAccountRepo = new AccountRepository(context);
        this.transformer = new CurrencyTransformer();
    }

    public boolean exists(Currency currency) {
        Cursor c = executeRaw(new CurrencyExistsQuery(currency));

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    public Currency get(long currencyId) throws CurrencyNotFoundException {
        Cursor c = executeRaw(new GetCurrencyQuery(currencyId));

        if (!c.moveToFirst())
            throw new CurrencyNotFoundException(currencyId);

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

    public void delete(Currency currency) throws CannotDeleteCurrencyException {
        if (mAccountRepo.isCurrencyAttachedToAccount(currency))
            throw new CannotDeleteCurrencyException(currency);

        mDatabase.delete(
                ExpensesDbHelper.TABLE_CURRENCIES,
                ExpensesDbHelper.CURRENCIES_COL_ID + " = ?",
                new String[]{"" + currency.getIndex()}
        );
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
