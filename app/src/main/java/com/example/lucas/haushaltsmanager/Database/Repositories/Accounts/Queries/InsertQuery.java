package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Queries;

import com.example.lucas.haushaltsmanager.Database.Common.IQuery;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Entities.Account.Account;
import com.example.lucas.haushaltsmanager.Entities.Price;

public class InsertQuery implements IQuery {
    private final Account account;

    public InsertQuery(Account account) {
        this.account = account;
    }

    @Override
    public String getQuery() {
        return "INSERT INTO "
                + ExpensesDbHelper.TABLE_ACCOUNTS + " ("
                + ExpensesDbHelper.ACCOUNTS_COL_NAME + ", "
                + ExpensesDbHelper.ACCOUNTS_COL_BALANCE + ", "
                + ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID + ")"
                + " VALUES (?, ?, ?)";
    }

    @Override
    public String[] getDefinition() {
        Price accountBalance = account.getBalance();

        return new String[]{
                account.getTitle(),
                Double.toString(accountBalance.getSignedValue()),
                Long.toString(accountBalance.getCurrency().getIndex())
        };
    }
}
