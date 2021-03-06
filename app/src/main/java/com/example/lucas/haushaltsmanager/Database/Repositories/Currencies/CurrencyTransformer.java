package com.example.lucas.haushaltsmanager.Database.Repositories.Currencies;

import android.database.Cursor;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Currency;

public class CurrencyTransformer implements TransformerInterface<Currency> {
    @Override
    public Currency transform(Cursor c) {
        long currencyId = c.getLong(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_ID));
        String currencyName = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_NAME));
        String currencyShortName = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME));
        String currencySymbol = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_SYMBOL));

        if (c.isLast())
            c.close();

        return new Currency(
                currencyId,
                currencyName,
                currencyShortName,
                currencySymbol
        );
    }
}
