package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts;

import android.database.Cursor;

import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Price;

import java.util.UUID;

public class AccountTransformer implements TransformerInterface<Account> {
    @Override
    public Account transform(Cursor c) {
        UUID accountId = getId(c);
        String accountName = c.getString(c.getColumnIndex("name"));
        double accountBalance = c.getDouble(c.getColumnIndex("balance"));

        if (c.isLast()) {
            c.close();
        }

        return new Account(
                accountId,
                accountName,
                new Price(accountBalance)
        );
    }

    private UUID getId(Cursor c) {
        int columnIndex = c.getColumnIndex("account_id");
        if (-1 == columnIndex) {
            columnIndex = c.getColumnIndex("id");
        }

        String rawId = c.getString(columnIndex);

        return UUID.fromString(rawId);
    }
}
