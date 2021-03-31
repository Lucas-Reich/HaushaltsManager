package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountCouldNotBeCreatedException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.CannotDeleteAccountException;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccountRepository implements AccountRepositoryInterface {
    private final String TABLE = "ACCOUNTS";

    private final SQLiteDatabase mDatabase;
    private final TransformerInterface<Account> transformer;

    public AccountRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
        transformer = new AccountTransformer();
    }

    public Account get(UUID accountId) throws AccountNotFoundException {
        Cursor c = executeRaw(new GetQuery(accountId));

        if (!c.moveToFirst()) {
            throw new AccountNotFoundException(accountId);
        }

        return transformer.transform(c);
    }

    public List<Account> getAll() {
        Cursor c = executeRaw(new GetAllAccountsQuery());

        ArrayList<Account> accounts = new ArrayList<>();
        while (c.moveToNext()) {
            accounts.add(transformer.transform(c));
        }

        return accounts;
    }

    public void insert(Account account) throws AccountCouldNotBeCreatedException {
        ContentValues values = new ContentValues();
        values.put("id", account.getId().toString());
        values.put("name", account.getTitle());
        values.put("balance", account.getBalance().getSignedValue());

        try {
            mDatabase.insertOrThrow(
                    TABLE,
                    null,
                    values
            );
        } catch (SQLException e) {
            throw new AccountCouldNotBeCreatedException(account, e);
        }
    }

    public void delete(Account account) throws CannotDeleteAccountException {
        try {
            mDatabase.delete(
                    TABLE,
                    "id = ?",
                    new String[]{account.getId().toString()}
            );
        } catch (SQLException e) {
            throw new CannotDeleteAccountException(account, e);
        }
    }

    public void update(Account account) throws AccountNotFoundException {
        ContentValues updatedAccount = new ContentValues();
        updatedAccount.put("name", account.getTitle());
        updatedAccount.put("balance", account.getBalance().getSignedValue());

        int affectedRows = mDatabase.update(
                TABLE,
                updatedAccount,
                "id = ?",
                new String[]{account.getId().toString()}
        );

        if (affectedRows == 0) {
            throw new AccountNotFoundException(account.getId());
        }
    }

    private Cursor executeRaw(QueryInterface query) {
        return mDatabase.rawQuery(String.format(
                query.sql(),
                query.values()
        ), null);
    }
}
