package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts;

import android.database.Cursor;

import com.example.lucas.haushaltsmanager.Database.Common.IQueryResult;
import com.example.lucas.haushaltsmanager.Database.Common.ITransformer;
import com.example.lucas.haushaltsmanager.Database.Exceptions.IllegalCursorException;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Account.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;

import java.util.Arrays;

public class AccountTransformer implements ITransformer<Account> {
    @Override
    public Account transform(IQueryResult queryResult) {
        if (!queryResult.moveToNext()) {
            return null;
        }

        return fromCursor(queryResult.getCurrent());
    }

    @Override
    public Account transformAndClose(IQueryResult queryResult) {
        Account createdAccount = transform(queryResult);

        queryResult.close();

        return createdAccount;
    }

    private Account fromCursor(Cursor c) {
        guardAgainstInvalidCursor(c);

        return new Account(
                c.getLong(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_ID)),
                c.getString(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_NAME)),
                c.getDouble(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_BALANCE)),
                buildCurrency(c)
        );
    }

    private Currency buildCurrency(Cursor c) {
        return new Currency(
                c.getLong(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_ID)),
                c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_NAME)),
                c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME)),
                c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_SYMBOL))
        );
    }

    private void guardAgainstInvalidCursor(Cursor c) throws IllegalCursorException {
        if (null == c) {
            throw IllegalCursorException.nullCursor(Account.class.getSimpleName(), null);
        }


        String[] columnNames = c.getColumnNames().clone();
        Arrays.sort(columnNames);

        assertArrayContains(columnNames, ExpensesDbHelper.ACCOUNTS_COL_ID);
        assertArrayContains(columnNames, ExpensesDbHelper.ACCOUNTS_COL_NAME);
        assertArrayContains(columnNames, ExpensesDbHelper.ACCOUNTS_COL_BALANCE);
        assertArrayContains(columnNames, ExpensesDbHelper.CURRENCIES_COL_ID);
        assertArrayContains(columnNames, ExpensesDbHelper.CURRENCIES_COL_NAME);
        assertArrayContains(columnNames, ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME);
        assertArrayContains(columnNames, ExpensesDbHelper.CURRENCIES_COL_SYMBOL);
    }

    private void assertArrayContains(String[] arr, String key) throws IllegalCursorException {
        int index = Arrays.binarySearch(arr, key);

        if (index < 0) {
            throw IllegalCursorException.missingField(key, null);
        }
    }
}
