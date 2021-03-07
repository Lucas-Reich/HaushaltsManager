package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Entities.Account;

class AccountExistsQuery implements QueryInterface {
    private final Account account;

    public AccountExistsQuery(Account account) {
        this.account = account;
    }

    @Override
    public String sql() {
        return "SELECT"
            + " *"
            + " FROM " + ExpensesDbHelper.TABLE_ACCOUNTS
            + " WHERE " + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_ID + " = %s"
            + " AND " + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_NAME + " = '%s'"
            + " AND " + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_BALANCE + " = %s"
            + " AND " + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID + " = %s"
            + " LIMIT 1;";
    }

    @Override
    public Object[] values() {
        return new Object[] {
            account.getIndex(),
            account.getTitle(),
            account.getBalance().getSignedValue(),
            account.getBalance().getCurrency().getIndex()
        };
    }
}
