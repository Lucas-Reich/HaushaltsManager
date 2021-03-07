package com.example.lucas.haushaltsmanager.Database.Repositories.Currencies;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;

class GetCurrencyQuery implements QueryInterface {
    private final long currencyId;

    public GetCurrencyQuery(long currencyId) {
        this.currencyId = currencyId;
    }

    @Override
    public String sql() {
        return "SELECT "
                + ExpensesDbHelper.CURRENCIES_COL_ID + ", "
                + ExpensesDbHelper.CURRENCIES_COL_NAME + ", "
                + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + ", "
                + ExpensesDbHelper.CURRENCIES_COL_SYMBOL
                + " FROM " + ExpensesDbHelper.TABLE_CURRENCIES
                + " WHERE " + ExpensesDbHelper.CURRENCIES_COL_ID + " = %s;";
    }

    @Override
    public Object[] values() {
        return new Object[]{
                currencyId
        };
    }
}
