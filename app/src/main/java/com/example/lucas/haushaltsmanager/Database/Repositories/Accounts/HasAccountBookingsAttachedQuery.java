package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Entities.Account;

class HasAccountBookingsAttachedQuery implements QueryInterface {
    private final Account account;

    public HasAccountBookingsAttachedQuery(Account account) {
        this.account = account;
    }

    @Override
    public String sql() {
        return "SELECT *"
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " WHERE %s.%s = %s" + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID + " = %s"
                + " LIMIT 1;";
    }

    @Override
    public Object[] values() {
        return new Object[]{
                account.getIndex()
        };
    }
}
