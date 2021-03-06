package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts;

import android.database.Cursor;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.CurrencyTransformer;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Price;

public class AccountTransformer implements TransformerInterface<Account> {
    private final CurrencyTransformer currencyTransformer;

    public AccountTransformer(CurrencyTransformer currencyTransformer) {
        this.currencyTransformer = currencyTransformer;
    }
    @Override
    public Account transform(Cursor c) {
        long accountId = c.getLong(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_ID));
        String accountName = c.getString(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_NAME));
        double accountBalance = c.getDouble(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_BALANCE));
        Currency accountCurrency = currencyTransformer.transform(c);

        if (c.isLast())
            c.close();

        return new Account(
            accountId,
            accountName,
            new Price(accountBalance, accountCurrency)
        );
    }
}
