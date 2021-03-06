package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.CannotDeleteAccountException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.CurrencyTransformer;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Currency;

import java.util.ArrayList;
import java.util.List;

public class AccountRepository implements AccountRepositoryInterface {
    private final SQLiteDatabase mDatabase;
    private final TransformerInterface<Account> transformer;

    public AccountRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
        transformer = new AccountTransformer(new CurrencyTransformer());
    }

    // TODO: This method is only used in testing
    public boolean exists(Account account) {
        Cursor c = executeRaw(new AccountExistsQuery(account));

        return isEmpty(c);
    }

    public Account get(long accountId) throws AccountNotFoundException {
        Cursor c = executeRaw(new GetQuery(accountId));

        if (!c.moveToFirst())
            throw new AccountNotFoundException(accountId);

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

    public Account insert(Account account) {
        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.ACCOUNTS_COL_NAME, account.getTitle());
        values.put(ExpensesDbHelper.ACCOUNTS_COL_BALANCE, account.getBalance().getSignedValue());
        values.put(ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID, account.getBalance().getCurrency().getIndex());

        long insertedAccountId = mDatabase.insert(ExpensesDbHelper.TABLE_ACCOUNTS, null, values);

        return new Account(
                insertedAccountId,
                account.getTitle(),
                account.getBalance()
        );
    }

    public void delete(Account account) throws CannotDeleteAccountException {
        if (hasBookingsAttached(account))
            throw new CannotDeleteAccountException(account);

        mDatabase.delete(ExpensesDbHelper.TABLE_ACCOUNTS, ExpensesDbHelper.ACCOUNTS_COL_ID + " = ?", new String[]{"" + account.getIndex()});
    }

    public void update(Account account) throws AccountNotFoundException {
        ContentValues updatedAccount = new ContentValues();
        updatedAccount.put(ExpensesDbHelper.ACCOUNTS_COL_NAME, account.getTitle());
        updatedAccount.put(ExpensesDbHelper.ACCOUNTS_COL_BALANCE, account.getBalance().getSignedValue());
        updatedAccount.put(ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID, account.getBalance().getCurrency().getIndex());

        int affectedRows = mDatabase.update(ExpensesDbHelper.TABLE_ACCOUNTS, updatedAccount, ExpensesDbHelper.ACCOUNTS_COL_ID + " = ?", new String[]{account.getIndex() + ""});

        if (affectedRows == 0) {
            throw new AccountNotFoundException(account.getIndex());
        }
    }

    public boolean isCurrencyAttachedToAccount(Currency currency) {
        Cursor c = executeRaw(new IsCurrencyAttachedToAccountQuery(currency));

        return !isEmpty(c);
    }

    public void closeDatabase() {

        DatabaseManager.getInstance().closeDatabase();
    }

    private boolean hasBookingsAttached(Account account) {
        Cursor c = executeRaw(new HasAccountBookingsAttachedQuery(account));

        return !isEmpty(c);
    }

    private Cursor executeRaw(QueryInterface query) {
        return mDatabase.rawQuery(String.format(
                query.sql(),
                query.values()
        ), null);
    }

    private boolean isEmpty(Cursor c) {
        int resultCount = c.getCount();
        c.close();

        return resultCount == 0;
    }

}
