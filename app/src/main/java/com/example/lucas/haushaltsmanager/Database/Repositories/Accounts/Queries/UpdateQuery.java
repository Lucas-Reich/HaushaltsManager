package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Queries;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Common.IQuery;
import com.example.lucas.haushaltsmanager.Entities.Account.Account;
import com.example.lucas.haushaltsmanager.Entities.Price;

public class UpdateQuery implements IQuery {
    private final Account account;

    public UpdateQuery(Account account) {
        this.account = account;
    }

    @Override
    public String getQuery() {
        return "UPDATE "
                + ExpensesDbHelper.TABLE_ACCOUNTS + " SET "
                + ExpensesDbHelper.ACCOUNTS_COL_NAME + " = ?, "
                + ExpensesDbHelper.ACCOUNTS_COL_BALANCE + " = ?, "
                + ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID + " = ? "
                + "WHERE " + ExpensesDbHelper.ACCOUNTS_COL_ID + " = ?";
    }

    @Override
    public String[] getDefinition() {
        Price balance = account.getBalance();

        return new String[]{
                account.getTitle(),
                Double.toString(balance.getSignedValue()),
                Long.toString(balance.getCurrency().getIndex()),
                Long.toString(account.getIndex())
        };
    }
}
