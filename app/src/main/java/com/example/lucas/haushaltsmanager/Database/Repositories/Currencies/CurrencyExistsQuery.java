package com.example.lucas.haushaltsmanager.Database.Repositories.Currencies;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Entities.Currency;

class CurrencyExistsQuery implements QueryInterface {
    private final Currency currency;

    public CurrencyExistsQuery(Currency currency) {
        this.currency = currency;
    }

    @Override
    public String sql() {
        return "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_CURRENCIES
                + " WHERE " + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_ID + " = %s"
                + " AND " + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_NAME + " = '%s'"
                + " AND " + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + " = '%s'"
                + " AND " + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SYMBOL + " = '%s'"
                + " LIMIT 1;";
    }

    @Override
    public Object[] values() {
        return new Object[]{
                currency.getIndex(),
                currency.getName(),
                currency.getShortName(),
                currency.getSymbol()
        };
    }
}
