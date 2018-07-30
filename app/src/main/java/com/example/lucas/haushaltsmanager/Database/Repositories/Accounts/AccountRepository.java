package com.example.lucas.haushaltsmanager.Database.Repositories.Accounts;

import android.content.ContentValues;
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

    public static boolean exists(Account account) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery;

        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_ACCOUNTS
                + " WHERE " + ExpensesDbHelper.ACCOUNTS_COL_NAME + " = '" + account.getTitle() + "'"
                + " AND " + ExpensesDbHelper.ACCOUNTS_COL_BALANCE + " = " + account.getBalance()
                + " AND " + ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID + " = " + account.getCurrency().getIndex()
                + " LIMIT 1;";

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {

            c.close();
            DatabaseManager.getInstance().closeDatabase();
            return true;
        }

        c.close();
        DatabaseManager.getInstance().closeDatabase();
        return false;
    }

    public static Account get(long accountId) throws AccountNotFoundException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

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

        Cursor c = db.rawQuery(selectQuery, null);

        if (!c.moveToFirst()) {
            throw new AccountNotFoundException(accountId);
        }

        Account account = cursorToAccount(c);

        c.close();
        DatabaseManager.getInstance().closeDatabase();
        return account;
    }

    public static List<Account> getAll() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

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

        Cursor c = db.rawQuery(selectQuery, null);
        c.moveToFirst();

        ArrayList<Account> accounts = new ArrayList<>();
        while (!c.isAfterLast()) {

            accounts.add(cursorToAccount(c));
            c.moveToNext();
        }

        c.close();
        DatabaseManager.getInstance().closeDatabase();
        return accounts;
    }

    public static Account insert(Account account) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.ACCOUNTS_COL_NAME, account.getTitle());
        values.put(ExpensesDbHelper.ACCOUNTS_COL_BALANCE, account.getBalance());
        values.put(ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID, account.getCurrency().getIndex());

        long insertedAccountId = db.insert(ExpensesDbHelper.TABLE_ACCOUNTS, null, values);
        DatabaseManager.getInstance().closeDatabase();

        return new Account(
                insertedAccountId,
                account.getTitle(),
                account.getBalance(),
                account.getCurrency()
        );
    }

    public static void delete(Account account) throws CannotDeleteAccountException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        if (isAttachedToBooking(account))
            throw new CannotDeleteAccountException(account);

        db.delete(ExpensesDbHelper.TABLE_ACCOUNTS, ExpensesDbHelper.ACCOUNTS_COL_ID + " = ?", new String[]{"" + account.getIndex()});
        DatabaseManager.getInstance().closeDatabase();
    }

    public static void update(Account account) throws AccountNotFoundException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ContentValues updatedAccount = new ContentValues();
        updatedAccount.put(ExpensesDbHelper.ACCOUNTS_COL_NAME, account.getTitle());
        updatedAccount.put(ExpensesDbHelper.ACCOUNTS_COL_BALANCE, account.getBalance());
        updatedAccount.put(ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID, account.getCurrency().getIndex());

        int affectedRows = db.update(ExpensesDbHelper.TABLE_ACCOUNTS, updatedAccount, ExpensesDbHelper.ACCOUNTS_COL_ID + " = ?", new String[]{account.getIndex() + ""});
        DatabaseManager.getInstance().closeDatabase();

        if (affectedRows == 0)
            throw new AccountNotFoundException(account.getIndex());
    }

    private static boolean isAttachedToBooking(Account account) {
        return isAttachedToParentBooking(account) || isAttachedToChildBooking(account);
    }

    private static boolean isAttachedToParentBooking(Account account) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery;
        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID + " = " + account.getIndex()
                + " LIMIT 1;";

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {

            c.close();
            DatabaseManager.getInstance().closeDatabase();
            return true;
        }

        c.close();
        DatabaseManager.getInstance().closeDatabase();
        return false;
    }

    private static boolean isAttachedToChildBooking(Account account) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery;
        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_CHILD_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_ACCOUNT_ID + " = " + account.getIndex()
                + " LIMIT 1;";

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {

            c.close();
            DatabaseManager.getInstance().closeDatabase();
            return true;
        }

        c.close();
        DatabaseManager.getInstance().closeDatabase();
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
