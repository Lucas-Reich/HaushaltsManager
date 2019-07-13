package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Queries;

import com.example.lucas.haushaltsmanager.Database.Common.IQuery;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;

public class DeleteQuery implements IQuery {
    private final long accountId;

    public DeleteQuery(long accountId) {
        this.accountId = accountId;
    }

    @Override
    public String getQuery() {
        return "DELETE FROM "
                + ExpensesDbHelper.TABLE_ACCOUNTS
                + " WHERE " + ExpensesDbHelper.ACCOUNTS_COL_ID + " = ?";
    }

    @Override
    public String[] getDefinition() {
        return new String[]{
                Long.toString(accountId)
        };
    }
}
