package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.AccountNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Accounts.Exceptions.CannotDeleteAccountException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.CurrencyRepository;
import com.example.lucas.haushaltsmanager.Entities.Account;

import java.util.ArrayList;
import java.util.List;

public class AccountRepository {
    private SQLiteDatabase mDatabase;

    public AccountRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();

    }

    public boolean exists(Account account) {

        String selectQuery;
        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_ACCOUNTS
                + " WHERE " + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_ID + " = " + account.getIndex()
                + " AND " + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_NAME + " = '" + account.getTitle() + "'"
                + " AND " + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_BALANCE + " = " + account.getBalance()
                + " AND " + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID + " = " + account.getCurrency().getIndex()
                + " LIMIT 1;";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    public Account get(long accountId) throws AccountNotFoundException {
        String selectQuery;
        selectQuery = "SELECT "
                + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_ID + ", "
                + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_BALANCE + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_ID + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SYMBOL
                + " FROM " + ExpensesDbHelper.TABLE_ACCOUNTS
                + " JOIN " + ExpensesDbHelper.TABLE_CURRENCIES + " ON " + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID + " = " + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_ID
                + " WHERE " + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_ID + " = " + accountId + ";";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (!c.moveToFirst()) {

            c.close();
            throw new AccountNotFoundException(accountId);
        }

        Account account = cursorToAccount(c);

        c.close();
        return account;
    }

    public List<Account> getAll() {
        String selectQuery;
        selectQuery = "SELECT "
                + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_ID + ", "
                + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_BALANCE + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_ID + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SYMBOL
                + " FROM " + ExpensesDbHelper.TABLE_ACCOUNTS
                + " JOIN " + ExpensesDbHelper.TABLE_CURRENCIES + " ON " + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID + " = " + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_ID
                + ";";

        Cursor c = mDatabase.rawQuery(selectQuery, null);
        c.moveToFirst();

        ArrayList<Account> accounts = new ArrayList<>();
        while (!c.isAfterLast()) {

            accounts.add(cursorToAccount(c));
            c.moveToNext();
        }

        c.close();
        return accounts;
    }

    public Account insert(Account account) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.ACCOUNTS_COL_NAME, account.getTitle());
        values.put(ExpensesDbHelper.ACCOUNTS_COL_BALANCE, account.getBalance());
        values.put(ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID, account.getCurrency().getIndex());

        long insertedAccountId = mDatabase.insert(ExpensesDbHelper.TABLE_ACCOUNTS, null, values);

        return new Account(
                insertedAccountId,
                account.getTitle(),
                account.getBalance(),
                account.getCurrency()
        );
    }

    public void delete(Account account) throws CannotDeleteAccountException {

        if (isAttachedToBooking(account))
            throw new CannotDeleteAccountException(account);

        mDatabase.delete(ExpensesDbHelper.TABLE_ACCOUNTS, ExpensesDbHelper.ACCOUNTS_COL_ID + " = ?", new String[]{"" + account.getIndex()});
    }

    public void update(Account account) throws AccountNotFoundException {

        ContentValues updatedAccount = new ContentValues();
        updatedAccount.put(ExpensesDbHelper.ACCOUNTS_COL_NAME, account.getTitle());
        updatedAccount.put(ExpensesDbHelper.ACCOUNTS_COL_BALANCE, account.getBalance());
        updatedAccount.put(ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID, account.getCurrency().getIndex());

        int affectedRows = mDatabase.update(ExpensesDbHelper.TABLE_ACCOUNTS, updatedAccount, ExpensesDbHelper.ACCOUNTS_COL_ID + " = ?", new String[]{account.getIndex() + ""});

        if (affectedRows == 0)
            throw new AccountNotFoundException(account.getIndex());
    }

    private boolean isAttachedToBooking(Account account) {
        return isAttachedToParentBooking(account) || isAttachedToChildBooking(account);
    }

    private boolean isAttachedToParentBooking(Account account) {

        String selectQuery;
        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID + " = " + account.getIndex()
                + " LIMIT 1;";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    private boolean isAttachedToChildBooking(Account account) {

        String selectQuery;
        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID + " = " + account.getIndex()
                + " LIMIT 1;";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    public static Account cursorToAccount(Cursor c) {
        long accountId = c.getLong(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_ID));
        String accountName = c.getString(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_NAME));
        double accountBalance = c.getDouble(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_BALANCE));

        return new Account(
                accountId,
                accountName,
                accountBalance,
                CurrencyRepository.cursorToCurrency(c)
        );
    }
}
