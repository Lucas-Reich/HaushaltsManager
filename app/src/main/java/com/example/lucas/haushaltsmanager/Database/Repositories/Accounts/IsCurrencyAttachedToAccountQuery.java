package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Entities.Currency;

class IsCurrencyAttachedToAccountQuery implements QueryInterface {
    private final Currency currency;

    public IsCurrencyAttachedToAccountQuery(Currency currency) {
        this.currency = currency;
    }

    public String sql() {
        return "SELECT *"
            + " FROM " + ExpensesDbHelper.TABLE_ACCOUNTS
            + " WHERE "
            + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID + " = %s"
            + " LIMIT 1";
    }

    public Object[] values() {
        return new Object[] {
            currency.getIndex()
        };
    }
}
