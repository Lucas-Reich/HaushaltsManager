package com.example.lucas.haushaltsmanager.Database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Database.Exceptions.CannotDeleteAccountException;
import com.example.lucas.haushaltsmanager.Database.Exceptions.CannotDeleteCategoryException;
import com.example.lucas.haushaltsmanager.Database.Exceptions.CannotDeleteCurrencyException;
import com.example.lucas.haushaltsmanager.Database.Exceptions.CannotDeleteTagException;
import com.example.lucas.haushaltsmanager.Database.Exceptions.EntityNotExistingException;
import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Tag;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpensesDataSource {
    private static final String TAG = ExpensesDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private ExpensesDbHelper dbHelper;
    private Context mContext;

    public ExpensesDataSource(Context context) {

        this.mContext = context;
        Log.d(TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new ExpensesDbHelper(context);
    }

    public void open() {

        database = dbHelper.getWritableDatabase();
        Log.d(TAG, "Opened db connection with dbHelper");
    }

    public void close() {

        dbHelper.close();
        Log.d(TAG, "Closed Db with the help of dbHelper.");
    }

    public boolean isOpen() {

        return database != null && database.isOpen();
    }

    @NonNull
    private ExpenseObject cursorToChildBooking(Cursor c) {

        int expenseId = c.getInt(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_ID));

        Calendar date = Calendar.getInstance();
        String dateString = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_DATE));
        date.setTimeInMillis(Long.parseLong(dateString));

        String title = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_TITLE));
        double price = c.getDouble(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_PRICE));
        boolean expenditure = c.getInt(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE)) == 1;
        String notice = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_NOTICE));
        int exchangeRate = c.getInt(c.getColumnIndex(ExpensesDbHelper.CHILD_BOOKINGS_COL_EXCHANGE_RATE));

        long categoryId = c.getLong(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID));
        String categoryName = c.getString(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_NAME));
        String categoryColor = c.getString(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_COLOR));
        boolean defaultExpenseType = c.getInt(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE)) == 1;
        Category category = new Category(categoryId, categoryName, categoryColor, defaultExpenseType);

        long curId = c.getLong(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID));
        String curName = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_NAME));
        String curShortName = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME));
        String curSymbol = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_SYMBOL));
        Double rateToBase = getRateToBase(curId, date.getTimeInMillis());
        Currency currency = new Currency(curId, curName, curShortName, curSymbol, rateToBase);

        long accountId = c.getLong(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID));
        String accountName = c.getString(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_NAME));
        double accountBalance = c.getDouble(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_BALANCE));
        Account account = new Account(accountId, accountName, accountBalance, currency);

        long currencyId = c.getLong(c.getColumnIndex(ExpensesDbHelper.CHILD_BOOKINGS_COL_CURRENCY_ID));
        Currency expenseCurrency = getCurrency(currencyId);
        expenseCurrency.setRateToBase(exchangeRate);


        ExpenseObject expense = new ExpenseObject(expenseId, title, price, date, expenditure, category, notice, account, expenseCurrency, ExpenseObject.EXPENSE_TYPES.CHILD_EXPENSE);

        expense.setTags(getTagsToBooking(expense.getIndex()));

        return expense;
    }

    /**
     * Convenience Method for mapping a Cursor to a Booking
     *
     * @param c Cursor object obtained by a SQLITE search query
     * @return An remapped ExpenseObject
     */
    private ExpenseObject cursorToExpense(Cursor c) {

        int expenseId = c.getInt(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_ID));

        Calendar date = Calendar.getInstance();
        String dateString = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_DATE));
        date.setTimeInMillis(Long.parseLong(dateString));

        String title = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_TITLE));

        double price = c.getDouble(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_PRICE));

        boolean expenditure = c.getInt(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE)) == 1;

        String notice = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_NOTICE));

        long categoryId = c.getLong(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID));
        String categoryName = c.getString(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_NAME));
        String categoryColor = c.getString(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_COLOR));
        boolean defaultExpenseType = c.getInt(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE)) == 1;
        Category category = new Category(categoryId, categoryName, categoryColor, defaultExpenseType);

        long curId = c.getLong(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID));
        String curName = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_NAME));
        String curShortName = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME));
        String curSymbol = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_SYMBOL));
        Double rateToBase = getRateToBase(curId, date.getTimeInMillis());
        Currency currency = new Currency(curId, curName, curShortName, curSymbol, rateToBase);

        long accountId = c.getLong(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID));
        String accountName = c.getString(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_NAME));
        double accountBalance = c.getDouble(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_BALANCE));
        Account account = new Account(accountId, accountName, accountBalance, currency);

        int exchangeRate = c.getInt(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_EXCHANGE_RATE));

        long currencyId = c.getLong(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID));
        Currency expenseCurrency = getCurrency(currencyId);
        expenseCurrency.setRateToBase(exchangeRate);

        ExpenseObject.EXPENSE_TYPES expense_type = ExpenseObject.EXPENSE_TYPES.valueOf(c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE)));

        ExpenseObject expense = new ExpenseObject(expenseId, title, price, date, expenditure, category, notice, account, expenseCurrency, expense_type);

        boolean isParent = c.getInt(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_IS_PARENT)) == 1;
        if (isParent) {

            //todo ist die spalte isParent noch notwendig, nachdem die spalte expense_type erstellt wurde?
            Log.d(TAG, "cursorToExpense: " + expenseId);
            expense.addChildren(getChildrenToParent(expenseId));
        }

        expense.setTags(getTagsToBooking(expense.getIndex()));

        return expense;
    }

    /**
     * Method for mapping an Cursor to a Category object
     *
     * @param c mDatabase cursor
     * @return Category object
     */
    @NonNull
    private Category cursorToCategory(Cursor c) {

        long categoryIndex = c.getLong(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_ID));
        String categoryName = c.getString(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_NAME));
        String categoryColor = c.getString(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_COLOR));
        boolean defaultExpenseType = c.getInt(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE)) == 1;

        return new Category(categoryIndex, categoryName, categoryColor, defaultExpenseType);
    }

    /**
     * Method for mapping an Cursor to a Currency object
     *
     * @param c mDatabase cursor
     * @return Currency object
     */
    @NonNull
    private Currency cursorToCurrency(Cursor c) {

        long currencyId = c.getLong(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_ID));
        String currencyName = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_NAME));
        String currencyShortName = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME));
        String currencySymbol = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_SYMBOL));

        return new Currency(currencyId, currencyName, currencyShortName, currencySymbol);
    }

    /**
     * Method for mapping an Cursor to a Account object
     *
     * @param c mDatabase cursor
     * @return Account object
     */
    @NonNull
    private Account cursorToAccount(Cursor c) {

        long accountIndex = c.getLong(0);
        String accountName = c.getString(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_NAME));
        double accountBalance = c.getDouble(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_BALANCE));

        long currencyID = c.getLong(3);
        String currencyName = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_NAME));
        String currencyShortName = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME));
        String currencySymbol = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_SYMBOL));

        Currency currency = new Currency(currencyID, currencyName, currencyShortName, currencySymbol, null);

        return new Account(accountIndex, accountName, accountBalance, currency);
    }

    /**
     * Method for mapping an Cursor to a Tag object
     *
     * @param c mDatabase cursor
     * @return Tag object
     */
    @NonNull
    private Tag cursorToTag(Cursor c) {

        long tagIndex = c.getLong(c.getColumnIndex(ExpensesDbHelper.TAGS_COL_ID));
        String tagName = c.getString(c.getColumnIndex(ExpensesDbHelper.TAGS_COL_NAME));

        return new Tag(tagIndex, tagName);
    }

    /**
     * Methode um eine Dummy Ausgabe zu erstellen.
     *
     * @return Dummy Ausgabe
     */
    private ExpenseObject createDummyExpense() {

        SharedPreferences preferences = mContext.getSharedPreferences("UserSettings", Context.MODE_PRIVATE);
        Currency currency = getCurrency(preferences.getLong("mainCurrencyIndex", 1));

        ExpenseObject dummyExpense = ExpenseObject.createDummyExpense(mContext);
        dummyExpense.setExpenseCurrency(currency);

        return createBooking(dummyExpense);
    }


    /**
     * Convenience Method for creating a new Account
     *
     * @param account Account object which should be created
     * @return the id of the created tag. -1 if the insertion failed
     */
    public Account createAccount(Account account) {

        if (account.getCurrency().getIndex() == -1)
            throw new RuntimeException("Cannot create account with dummy Currency object!");

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.ACCOUNTS_COL_NAME, account.getName());
        values.put(ExpensesDbHelper.ACCOUNTS_COL_BALANCE, account.getBalance());
        values.put(ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID, account.getCurrency().getIndex());

        Log.d(TAG, "Creating account: " + account.getName());
        long index = database.insert(ExpensesDbHelper.TABLE_ACCOUNTS, null, values);

        return new Account(index, account.getName(), account.getBalance(), account.getCurrency());
    }

    /**
     * Convenience Method for getting an specific Account
     *
     * @param accountId id of account
     * @return account object if available else null
     */
    @Nullable
    public Account getAccountById(long accountId) {

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
        Log.d(TAG, "getAccountById: " + selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getAccountById: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        return c.isAfterLast() ? null : cursorToAccount(c);
    }

    /**
     * Method for getting an account by its getTitle
     *
     * @param accountName getTitle of account
     * @return account object if available else null
     */
    @Nullable
    public Account getAccountByName(String accountName) {

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
                + " WHERE " + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_NAME + " = '" + accountName + "';";
        Log.d(TAG, "getAccountById: " + selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getAccountByName: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        return c.isAfterLast() ? null : cursorToAccount(c);
    }

    public ArrayList<Account> getAllAccounts() {

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
        Log.d(TAG, "getAccountById: " + selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getAllAccounts: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        ArrayList<Account> accounts = new ArrayList<>();
        while (!c.isAfterLast()) {

            accounts.add(cursorToAccount(c));
            c.moveToNext();
        }

        return accounts;
    }

    /**
     * Methode um die Werte eins Kontos anzupassen
     *
     * @param account Konto mit geänderten Werten
     * @return True bei erfolg, false bei Fehlschlag
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean updateAccount(Account account) {

        ContentValues updatedAccount = new ContentValues();
        updatedAccount.put(ExpensesDbHelper.ACCOUNTS_COL_NAME, account.getName());
        updatedAccount.put(ExpensesDbHelper.ACCOUNTS_COL_BALANCE, account.getBalance());
        updatedAccount.put(ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID, account.getCurrency().getIndex());

        int affectedRows = database.update(ExpensesDbHelper.TABLE_ACCOUNTS, updatedAccount, ExpensesDbHelper.ACCOUNTS_COL_ID + " = ?", new String[]{account.getIndex() + ""});
        return affectedRows == 1;
    }

    /**
     * Methode um den Kontostand eines Kontos anzupassen.
     *
     * @param accountId Konot das angepasst werden soll
     * @param balance   Wert, welcher gutgeschrieben oder abgezogen werden soll
     * @return TRUE wenn die aktion erflogreich war, FALSE wenn nicht
     */
    public boolean updateAccountBalance(long accountId, double balance) {

        Account account = getAccountById(accountId);
        double newBalance = account.getBalance() + balance;

        ContentValues updatedAccount = new ContentValues();
        updatedAccount.put(ExpensesDbHelper.ACCOUNTS_COL_BALANCE, newBalance);

        int affectedRows = database.update(ExpensesDbHelper.TABLE_ACCOUNTS, updatedAccount, ExpensesDbHelper.ACCOUNTS_COL_ID + " = ?", new String[]{accountId + ""});
        return affectedRows == 1;
    }

    /**
     * Convenience Method for deleting a Account
     *
     * @param accountId Account object which should be deleted
     * @return the number of affected rows
     * @throws CannotDeleteAccountException Wenn ein Konto immer noch zu Buchungen zugewiesen ist kann es nicht gelöscht werden
     */
    @SuppressWarnings("UnusedReturnValue")
    public int deleteAccount(long accountId) throws CannotDeleteAccountException {

        if (hasAccountBookings(accountId))
            throw new CannotDeleteAccountException("Account with existing bookings cannot be deleted!");

        Log.d(TAG, "Deleting account at index: " + accountId);
        return database.delete(ExpensesDbHelper.TABLE_ACCOUNTS, ExpensesDbHelper.ACCOUNTS_COL_ID + " = ?", new String[]{"" + accountId});
    }

    /**
     * Methode um zu checken ob noch mindestends eine Buchung mit diesem Konto existiert
     *
     * @param accountId Id des Kontos
     * @return Boolean
     */
    private boolean hasAccountBookings(long accountId) {

        return isEntityAssignedToBooking(accountId, ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID);
    }


    /**
     * Methode um ein neues Tag zu erstellen
     *
     * @param tag Tag welches erstellt werden soll
     * @return Das gerade gespeicherte Tag
     */
    public Tag createTag(Tag tag) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.TAGS_COL_NAME, tag.getName());

        long index = database.insert(ExpensesDbHelper.TABLE_TAGS, null, values);
        Log.d(TAG, "created tag: " + tag);
        return new Tag(index, tag.getName());
    }

    /**
     * Methode um ein Tag mit einer bestimmten ID aus der Datenbank zu holen.
     *
     * @param tagId Id of the tag which should be selected
     * @return The tag at the specified index
     */
    @Nullable
    public Tag getTagById(long tagId) {

        String selectQuery;
        selectQuery = "SELECT "
                + ExpensesDbHelper.TAGS_COL_ID + ", "
                + ExpensesDbHelper.TAGS_COL_NAME
                + " FROM " + ExpensesDbHelper.TABLE_TAGS
                + " WHERE " + ExpensesDbHelper.TAGS_COL_ID + " = " + tagId;
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getTagById: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        return c.isAfterLast() ? null : cursorToTag(c);
    }

    /**
     * Convenience Method for getting all available Tags
     *
     * @return An array of strings with all Tags inside TABLE_TAGS
     */
    public List<Tag> getAllTags() {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.TAGS_COL_ID + ", "
                + ExpensesDbHelper.TAGS_COL_NAME
                + " FROM " + ExpensesDbHelper.TABLE_TAGS + ";";
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getAllTags: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        List<Tag> tags = new ArrayList<>();
        while (!c.isAfterLast()) {

            tags.add(cursorToTag(c));
            c.moveToNext();
        }

        return tags;
    }

    /**
     * Methode um die Werte eines Tags anzupassen.
     *
     * @param tag Tag mit angepassten Werten
     * @return True bei Erfolg, False bei fehlschlag
     */
    public boolean updateTag(Tag tag) {

        ContentValues updatedTag = new ContentValues();
        updatedTag.put(ExpensesDbHelper.TAGS_COL_NAME, tag.getName());

        int affectedRows = database.update(ExpensesDbHelper.TABLE_TAGS, updatedTag, ExpensesDbHelper.TAGS_COL_ID + " = ?", new String[]{tag.getIndex() + ""});
        return affectedRows == 1;
    }

    /**
     * Methode um ein Tag zu löschen
     *
     * @param tagId Id des zu löschenden Tags
     * @return Status der Operation
     * @throws CannotDeleteTagException Ein Tag kann nicht gelöscht werden, wenn es noch Buchungen mit diesem Tag gibt
     */
    public int deleteTag(long tagId) throws CannotDeleteTagException {

        if (hasTagBookings(tagId))
            throw new CannotDeleteTagException("Cannot delete tag while there are still Bookings with this tag");

        Log.d(TAG, "deleted tag at index " + tagId);
        return database.delete(ExpensesDbHelper.TABLE_TAGS, ExpensesDbHelper.TAGS_COL_ID + " = ?", new String[]{"" + tagId});
    }

    /**
     * Methode um herauszufinden ob es Buchungen mit diesem Tag gibt
     *
     * @param tagId Id des zu überprüfenden Tags
     * @return boolean
     */
    private boolean hasTagBookings(long tagId) {

        //TODO erstelle funktionalität
        return false;
    }


    /**
     * Interne Methode um ein Tag zu einer Buchung hinzuzufügen
     *
     * @param bookingId Id of the booking where the id has to be assigned to
     * @param tag       Tag welcher der angegebenen Buchung hinzugefügt werden soll
     * @return the index of the inserted row
     */
    @SuppressWarnings("UnusedReturnValue")
    private long assignTagToBooking(long bookingId, Tag tag) {

        if (tag.getIndex() == -1)
            tag = createTag(tag);

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_ID, tag.getIndex());
        values.put(ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_ID, bookingId);

        Log.d(TAG, "assigning tag " + tag.getName() + " to booking " + bookingId);
        return database.insert(ExpensesDbHelper.TABLE_BOOKINGS_TAGS, null, values);
    }

    /**
     * Interne Methode um alle Tags zu einer Buchung zu bekommen.
     *
     * @param bookingId The id of the Booking where the Tags should be outputted
     * @return All Tags to the specified booking in an String[]
     */
    private List<Tag> getTagsToBooking(long bookingId) {

        String selectQuery;
        selectQuery = "SELECT "
                + ExpensesDbHelper.TABLE_TAGS + "." + ExpensesDbHelper.TAGS_COL_ID + ", "
                + ExpensesDbHelper.TABLE_TAGS + "." + ExpensesDbHelper.TAGS_COL_NAME
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS_TAGS
                + " JOIN " + ExpensesDbHelper.TABLE_TAGS + " ON " + ExpensesDbHelper.TABLE_BOOKINGS_TAGS + "." + ExpensesDbHelper.BOOKINGS_TAGS_COL_TAG_ID + " = " + ExpensesDbHelper.TABLE_TAGS + "." + ExpensesDbHelper.TAGS_COL_ID
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS_TAGS + "." + ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_ID + " = " + bookingId;
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        c.moveToFirst();

        List<Tag> tags = new ArrayList<>();
        while (!c.isAfterLast()) {

            tags.add(cursorToTag(c));
            c.moveToNext();
        }

        return tags;
    }

    /**
     * Class internal Method for requesting all Bookings to a specified Tag
     *
     * @param tag Tag zu welchem alle Buchungen herausgefunden werden sollen
     * @return All ids of the affected Bookings
     */
    private List<ExpenseObject> getBookingsToTag(Tag tag) {

        //TODO implement
        return new ArrayList<>();
    }

    /**
     * Method for removing tag from booking
     *
     * @param bookingId id of booking
     * @param tagId     id of tag
     * @return result of operation
     */
    private int removeTagFromBooking(long bookingId, long tagId) {

        Log.d(TAG, "removing tag: " + tagId + " from booking: " + bookingId);
        String whereClause = ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_ID + " = ? AND " + ExpensesDbHelper.BOOKINGS_TAGS_COL_TAG_ID + " = ?";
        String[] whereArgs = new String[]{"" + bookingId, "" + tagId};

        return database.delete(ExpensesDbHelper.TABLE_BOOKINGS_TAGS, whereClause, whereArgs);
    }

    /**
     * Methode um alle zugewiesenden Tags einer Buchung zu löschen
     *
     * @param bookingId Buchung, deren Tags entfernt werden sollen.
     * @return Status der Operation
     */
    @SuppressWarnings("UnusedReturnValue")
    private int removeTagsFromBooking(long bookingId) {

        Log.d(TAG, "removeTagsFromBooking: removing tags from booking " + bookingId);
        return database.delete(ExpensesDbHelper.TABLE_BOOKINGS_TAGS, ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_ID + " = ?", new String[]{"" + bookingId});
    }


    /**
     * Method for creating multiple bookings at once
     *
     * @param bookings List of bookings
     */
    public void createBookings(List<ExpenseObject> bookings) {
        for (ExpenseObject booking : bookings)
            createBooking(booking);
    }

    /**
     * Methode um eine neue Buchung in die Datenbank zu schreiben
     *
     * @param expense The expense which has to be stored in the DB
     * @return Id of the created Booking
     */
    public ExpenseObject createBooking(ExpenseObject expense) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE, expense.getExpenseType().name());
        values.put(ExpensesDbHelper.BOOKINGS_COL_PRICE, expense.getUnsignedPrice());
        values.put(ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID, expense.getCategory().getIndex());
        values.put(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE, expense.isExpenditure());
        values.put(ExpensesDbHelper.BOOKINGS_COL_TITLE, expense.getTitle());
        values.put(ExpensesDbHelper.BOOKINGS_COL_DATE, expense.getDateTime().getTimeInMillis());
        values.put(ExpensesDbHelper.BOOKINGS_COL_NOTICE, expense.getNotice());
        values.put(ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID, expense.getAccount().getIndex());
        values.put(ExpensesDbHelper.BOOKINGS_COL_EXCHANGE_RATE, expense.getExchangeRate());
        values.put(ExpensesDbHelper.BOOKINGS_COL_IS_PARENT, expense.isParent());
        values.put(ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID, expense.getExpenseCurrency().getIndex());

        long index = database.insert(ExpensesDbHelper.TABLE_BOOKINGS, null, values);
        Log.d(TAG, "created expense at index: " + index);

        for (Tag tag : expense.getTags()) {
            assignTagToBooking(index, tag);
        }

        for (ExpenseObject child : expense.getChildren())
            addChild(child, index);

        updateAccountBalance(expense.getAccount().getIndex(), expense.getSignedPrice());

        return new ExpenseObject(index, expense.getTitle(), expense.getUnsignedPrice(), expense.getDateTime(), expense.isExpenditure(), expense.getCategory(), expense.getNotice(), expense.getAccount(), expense.getExpenseCurrency(), expense.getExpenseType());
    }

    /**
     * Methode um eine Buchung aus der Datenbank zu holen
     *
     * @param bookingId Id der Buchung
     * @return Die angefrage Buchung
     */
    public ExpenseObject getBookingById(long bookingId) {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ID + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_PRICE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_TITLE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_DATE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_NOTICE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_EXCHANGE_RATE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_IS_PARENT + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID + ", "
                + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_BALANCE + ", "
                + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SYMBOL + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID + ", "
                + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " LEFT JOIN " + ExpensesDbHelper.TABLE_CATEGORIES + " ON " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID + " = " + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_ID
                + " LEFT JOIN " + ExpensesDbHelper.TABLE_ACCOUNTS + " ON " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID + " = " + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_ID
                + " LEFT JOIN " + ExpensesDbHelper.TABLE_CURRENCIES + " ON " + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID + " = " + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_ID
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ID + " = " + bookingId
                + " ORDER BY " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_DATE + " DESC;";
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getBookingById: " + DatabaseUtils.dumpCursorToString(c));

        c.moveToFirst();
        return cursorToExpense(c);
    }

    /**
     * Get all available bookings
     *
     * @return ExpenseObject ArrayList
     */
    public ArrayList<ExpenseObject> getBookings() {

        return getBookings(0, Calendar.getInstance().getTimeInMillis());
    }

    /**
     * Method for receiving all bookings in a specified date range
     *
     * @param startDateInMills startind date
     * @param endDateInMills   ending date
     * @return list of Expenses which are between the starting and end date
     */
    public ArrayList<ExpenseObject> getBookings(long startDateInMills, long endDateInMills) {

        String selectQuery;
        selectQuery = "SELECT "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ID + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_PRICE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_TITLE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_DATE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_NOTICE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_EXCHANGE_RATE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_IS_PARENT + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID + ", "
                + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_BALANCE + ", "
                + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SYMBOL + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID + ", "
                + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " LEFT JOIN " + ExpensesDbHelper.TABLE_CATEGORIES + " ON " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID + " = " + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_ID
                + " LEFT JOIN " + ExpensesDbHelper.TABLE_ACCOUNTS + " ON " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID + " = " + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_ID
                + " LEFT JOIN " + ExpensesDbHelper.TABLE_CURRENCIES + " ON " + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID + " = " + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_ID
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_DATE + " BETWEEN " + startDateInMills + " AND " + endDateInMills;
        selectQuery += " ORDER BY " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_DATE + " DESC;";
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getBookings: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        ArrayList<ExpenseObject> bookings = new ArrayList<>();
        while (!c.isAfterLast()) {

            bookings.add(cursorToExpense(c));
            c.moveToNext();
        }

        return bookings;
    }

    /**
     * Methode um eine Ausgabe in der Datenbank zu updaten.
     *
     * @param expense Ausgabe mit geupdateten Werten.
     * @return True bei Erfolg, False bei Fehlschlag.
     */
    @SuppressWarnings("UnusedReturnValue")
    public Boolean updateBooking(ExpenseObject expense) {

        //todo 'schlauen' algorithmus entwickeln welcher nur die geänderten stellen in der datenbank updated
        ContentValues updatedExpense = new ContentValues();
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE, expense.getExpenseType().name());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_PRICE, expense.getCalcPrice());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID, expense.getCategory().getIndex());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE, expense.isExpenditure());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_TITLE, expense.getTitle());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_DATE, expense.getDateTime().getTimeInMillis());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_NOTICE, expense.getNotice());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID, expense.getAccount().getIndex());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_EXCHANGE_RATE, expense.getExchangeRate());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_IS_PARENT, expense.isParent());
        updatedExpense.put(ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID, expense.getExpenseCurrency().getIndex());

        //todo wenn der preis der Buchung angepasst wurde muss sich auch der Kontostand des Kontos anpassen
        removeTagsFromBooking(expense.getIndex());
        for (Tag tag : expense.getTags()) {
            assignTagToBooking(expense.getIndex(), tag);
        }

        int affectedRows = database.update(ExpensesDbHelper.TABLE_BOOKINGS, updatedExpense, ExpensesDbHelper.BOOKINGS_COL_ID + " = ?", new String[]{expense.getIndex() + ""});
        return affectedRows == 1;
    }

    /**
     * Methode um mehere Buchungen auf einmal zu löschen
     *
     * @param bookingIds array of booking ids
     * @return True bei erfolg, False bei fehlschlag.
     *///todo wenn es einen fehler beim löschen einer buchung geben sollte muss etwas spezielles passieren
    @SuppressWarnings("UnusedReturnValue")
    public boolean deleteBookings(long bookingIds[]) {
        boolean result = true;
        for (long bookingId : bookingIds)
            result = result && deleteBooking(bookingId);

        return result;
    }

    /**
     * Methode um eine Buchung zu löschen
     *
     * @param bookingId Id of the Booking which should be deleted
     * @return TRUE bei Erfolg, FALSE bei Fehlschlag
     */
    public boolean deleteBooking(long bookingId) {
        Log.d(TAG, "deleteBooking: deleting booking " + bookingId);

        removeTagsFromBooking(bookingId);
        deleteChildrenFromParent(bookingId);

        int affectedRows = database.delete(ExpensesDbHelper.TABLE_BOOKINGS, ExpensesDbHelper.BOOKINGS_COL_ID + " = ?", new String[]{"" + bookingId});
        return affectedRows == 1;
    }


    /**
     * Methode um eine Kindbuchung zu einer Buchung hinzuzufügen
     *
     * @param child    child to append to parent
     * @param parentId Id of parent booking
     * @return index of inserted child
     */
    @SuppressWarnings("UnusedReturnValue")
    private long addChild(ExpenseObject child, long parentId) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_EXPENSE_TYPE, child.getExpenseType().name());
        values.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_PRICE, child.getUnsignedPrice());
        values.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_PARENT_BOOKING_ID, parentId);
        values.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_CATEGORY_ID, child.getCategory().getIndex());
        values.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_EXPENDITURE, child.isExpenditure());
        values.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_TITLE, child.getTitle());
        values.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_DATE, child.getDateTime().getTimeInMillis());
        values.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_NOTICE, child.getNotice());
        values.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_ACCOUNT_ID, child.getAccount().getIndex());
        values.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_EXCHANGE_RATE, child.getExchangeRate());
        values.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_CURRENCY_ID, child.getExpenseCurrency().getIndex());

        long childId = database.insert(ExpensesDbHelper.TABLE_CHILD_BOOKINGS, null, values);
        Log.d(TAG, "created child expense at index: " + childId);

        updateAccountBalance(child.getAccount().getIndex(), child.getSignedPrice());

        for (Tag tag : child.getTags())
            assignTagToBooking(childId, tag);

        return childId;
    }

    /**
     * Functions ensures that the parent expense is no child itself.
     * It also checks whether the parent expense has children attached to it or not.
     * If the parent expense has no children then a dummy expense is created and the parent and the child are attached to it.
     *
     * @param childExpense  expense to add to the parent
     * @param parentBooking Übergeordnete Buchung
     * @return Übergeordnete Buchung
     *///Todo methode noch einmal überarbeiten
    @SuppressWarnings("UnusedReturnValue")
    public ExpenseObject addChildToBooking(ExpenseObject childExpense, ExpenseObject parentBooking) {

        if (isChild(parentBooking.getIndex()))
            throw new UnsupportedOperationException("Cannot add Booking to an child Booking");

        if (parentBooking.isParent()) {//wenn ich zu einer bestehenden Parent buchung ein weiteres kind hinzufügen möchte

            parentBooking.addChild(childExpense);
            addChild(childExpense, parentBooking.getIndex());
            return parentBooking;
        } else {// wenn ich zu einer bestehenden buchung ein kind hinzufügen möchte

            ExpenseObject dummyParentBooking = createDummyExpense();
            dummyParentBooking.addChild(parentBooking);
            dummyParentBooking.addChild(childExpense);
            updateBooking(dummyParentBooking);

            addChild(parentBooking, dummyParentBooking.getIndex());
            addChild(childExpense, dummyParentBooking.getIndex());

            deleteBooking(parentBooking.getIndex());

            return dummyParentBooking;
        }
    }

    /**
     * Methode um eine Liste von Buchungen zusammenzufügen.
     * Dabei wird zuesrt eine DummyParent erstellt, unter welchem dann die Ausgaben platziert werden
     *
     * @param childExpenses Buchungen die zusammengefügt werden sollen
     * @return Parent der Kindbuchungen
     */
    public ExpenseObject combineChildBookings(List<ExpenseObject> childExpenses) {

        ExpenseObject parentBooking = createDummyExpense();//Dummyausgabe wird erstellt und in der Datenbank gespeichert (jedoch nicht als parent, da zur zeit der erstellung keine Kinder dabei sind)
        parentBooking.addChildren(childExpenses);//Der Dummyausgabe werden kinder hizugefügt
        updateBooking(parentBooking);//Nun ist die Dummyausgabe ein Parent, was auch in der Datenbank gepeichert/geupdated werden muss

        for (ExpenseObject child : childExpenses) {

            addChild(child, parentBooking.getIndex());
            deleteBooking(child.getIndex());
        }

        return parentBooking;
    }

    /**
     * Methode um zu einer Buchung alle Kindbuchungen zu erhalten.
     *
     * @param parentId Id der ParentBuchung
     * @return Liste der Kinder
     */
    public ArrayList<ExpenseObject> getChildrenToParent(long parentId) {

        String selectQuery;
        selectQuery = "SELECT "
                + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_EXPENSE_TYPE + ", "
                + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_ID + ", "
                + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_PARENT_BOOKING_ID + ", "
                + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_PRICE + ", "
                + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_EXPENDITURE + ", "
                + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_TITLE + ", "
                + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_DATE + ", "
                + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_NOTICE + ", "
                + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_EXCHANGE_RATE + ", "
                + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_CATEGORY_ID + ", "
                + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE + ", "
                + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_ACCOUNT_ID + ", "
                + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_BALANCE + ", "
                + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SYMBOL + ", "
                + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_CURRENCY_ID
                + " FROM " + ExpensesDbHelper.TABLE_CHILD_BOOKINGS
                + " LEFT JOIN " + ExpensesDbHelper.TABLE_CATEGORIES + " ON " + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_CATEGORY_ID + " = " + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_ID
                + " LEFT JOIN " + ExpensesDbHelper.TABLE_ACCOUNTS + " ON " + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_ACCOUNT_ID + " = " + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_ID
                + " LEFT JOIN " + ExpensesDbHelper.TABLE_CURRENCIES + " ON " + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID + " = " + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_ID
                + " WHERE " + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_PARENT_BOOKING_ID + " = " + parentId
                + " ORDER BY " + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_DATE + " DESC;";
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getChildrenToParent: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        ArrayList<ExpenseObject> childBookings = new ArrayList<>();
        while (!c.isAfterLast()) {

            childBookings.add(cursorToChildBooking(c));
            c.moveToNext();
        }

        return childBookings;
    }

    /**
     * Methode um eine bestimmte Kindbuchung zu erhalten.
     *
     * @param childId Id der Kindbuchung
     * @return Kindbuchung
     */
    public ExpenseObject getChildBookingById(long childId) {

        String selectQuery;
        selectQuery = "SELECT "
                + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_EXPENSE_TYPE + ", "
                + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_ID + ", "
                + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_PARENT_BOOKING_ID + ", "
                + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_PRICE + ", "
                + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_EXPENDITURE + ", "
                + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_TITLE + ", "
                + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_DATE + ", "
                + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_NOTICE + ", "
                + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_EXCHANGE_RATE + ", "
                + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_CATEGORY_ID + ", "
                + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE + ", "
                + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_ACCOUNT_ID + ", "
                + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_BALANCE + ", "
                + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_CURRENCY_ID + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + ", "
                + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_SYMBOL + ", "
                + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_CURRENCY_ID
                + " FROM " + ExpensesDbHelper.TABLE_CHILD_BOOKINGS
                + " LEFT JOIN " + ExpensesDbHelper.TABLE_CATEGORIES + " ON " + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_CATEGORY_ID + " = " + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_ID
                + " LEFT JOIN " + ExpensesDbHelper.TABLE_ACCOUNTS + " ON " + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_ACCOUNT_ID + " = " + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_ID
                + " LEFT JOIN " + ExpensesDbHelper.TABLE_CURRENCIES + " ON " + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID + " = " + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_ID
                + " WHERE " + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_ID + " = " + childId
                + " ORDER BY " + ExpensesDbHelper.TABLE_CHILD_BOOKINGS + "." + ExpensesDbHelper.CHILD_BOOKINGS_COL_DATE + " DESC;";
        Log.d(TAG, "getChildBookingById: " + selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getChildBookingById: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        return cursorToChildBooking(c);
    }

    /**
     * Methode um herauszufinden ob eine Buchung eine Kindbuchung ist
     *
     * @param expenseId Id der zu überprüfenden Buchung
     * @return TRUE, wenn die Buchung eine Kindbuchung ist. FALSE, wenn nicht.
     */
    private boolean isChild(long expenseId) {

        String selectQuery = "SELECT"
                + " COUNT(1) 'exists'"
                + " FROM " + ExpensesDbHelper.TABLE_CHILD_BOOKINGS
                + " WHERE " + ExpensesDbHelper.CHILD_BOOKINGS_COL_ID + " = " + expenseId + ";";
        Log.d(TAG, "isChild: " + selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        c.moveToFirst();

        boolean result = c.getInt(c.getColumnIndex("exists")) != 0;
        c.close();

        return result;
    }

    /**
     * Methode um ein Kindbuchung zu updaten.
     * Dabei wird die parent buchung aber nicht mit geupdated.
     *
     * @param child Kindbuchung mit neuen Werten
     * @return True bei erfolg, false bei fehlschlag
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean updateChildBooking(ExpenseObject child) {

        ContentValues updatedChild = new ContentValues();
        updatedChild.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_EXPENSE_TYPE, child.getExpenseType().name());
        updatedChild.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_PRICE, child.getUnsignedPrice());
        updatedChild.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_CATEGORY_ID, child.getCategory().getIndex());
        updatedChild.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_EXPENDITURE, child.isExpenditure());
        updatedChild.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_TITLE, child.getTitle());
        updatedChild.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_DATE, child.getDateTime().getTimeInMillis());
        updatedChild.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_NOTICE, child.getNotice());
        updatedChild.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_ACCOUNT_ID, child.getAccount().getIndex());
        updatedChild.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_EXCHANGE_RATE, child.getExchangeRate());
        updatedChild.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_CURRENCY_ID, child.getExpenseCurrency().getIndex());


        //todo wenn der preis einer Kindbuchung angepasst wurde muss auch der Kontostand des zugehörigen Kontos angepasst werden
        int affectedRows = database.update(ExpensesDbHelper.TABLE_CHILD_BOOKINGS, updatedChild, ExpensesDbHelper.CHILD_BOOKINGS_COL_ID + " = ?", new String[]{child.getIndex() + ""});
        return affectedRows == 1;
    }

    public int deleteChildBooking(long childId) {

        //TODO wenn das kind das letzte des parents war muss der parent wieder als normale buchung eingefügt werden
        Log.d(TAG, "deleted child booking at index " + childId);
        return database.delete(ExpensesDbHelper.TABLE_CHILD_BOOKINGS, ExpensesDbHelper.CHILD_BOOKINGS_COL_ID + " = ?", new String[]{"" + childId});
    }

    /**
     * Methode die Kinder von einer Parentbuchung zu entfernen
     *
     * @param parentId ParentBuchung derer Kinder entfertn werden sollen
     * @return Status der Operation
     */
    @SuppressWarnings("UnusedReturnValue")
    public int deleteChildrenFromParent(long parentId) {

        Log.d(TAG, "deleteChildrenFromParent: deleting children with parent id " + parentId);
        return database.delete(ExpensesDbHelper.TABLE_CHILD_BOOKINGS, ExpensesDbHelper.CHILD_BOOKINGS_COL_PARENT_BOOKING_ID + " = ?", new String[]{"" + parentId});
    }


    public Category createCategory(Category category) {

        //TODO erstelle neue Kategorie wenn sie nicht bereits existiert
        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.CATEGORIES_COL_NAME, category.getName());
        values.put(ExpensesDbHelper.CATEGORIES_COL_COLOR, category.getColor());
        values.put(ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE, category.getDefaultExpenseType() ? 1 : 0);
        Log.d(TAG, "created new CATEGORY");


        long index = database.insert(ExpensesDbHelper.TABLE_CATEGORIES, null, values);
        return new Category(index, category.getName(), category.getColor(), category.getDefaultExpenseType());
    }

    public ArrayList<Category> getAllCategories() {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CATEGORIES_COL_ID + ", "
                + ExpensesDbHelper.CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE
                + " FROM " + ExpensesDbHelper.TABLE_CATEGORIES + ";";
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        c.moveToFirst();

        ArrayList<Category> allCategories = new ArrayList<>();
        Log.d(TAG, "getAllCategories: " + DatabaseUtils.dumpCursorToString(c));
        while (!c.isAfterLast()) {

            allCategories.add(cursorToCategory(c));
            c.moveToNext();
        }

        return allCategories;
    }

    /**
     * Convenience Method for getting a Category by its getTitle
     *
     * @param category Name of the CATEGORY
     * @return Returns an Category object
     */
    @Nullable
    public Category getCategoryByName(String category) {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CATEGORIES_COL_ID + ", "
                + ExpensesDbHelper.CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE
                + " FROM " + ExpensesDbHelper.TABLE_TAGS
                + " WHERE " + ExpensesDbHelper.CATEGORIES_COL_NAME + " = '" + category + "';";
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getCategoryByName: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        return c.isAfterLast() ? null : cursorToCategory(c);
    }

    /**
     * Convenience Method for getting a Category
     *
     * @param categoryId index of the desired Category
     * @return Returns an Category object
     */
    public Category getCategoryById(long categoryId) {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CATEGORIES_COL_ID + ", "
                + ExpensesDbHelper.CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE
                + " FROM " + ExpensesDbHelper.TABLE_TAGS
                + " WHERE " + ExpensesDbHelper.CATEGORIES_COL_ID + " = " + categoryId + ";";
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getCategoryById: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        return c.isAfterLast() ? null : cursorToCategory(c);
    }

    /**
     * Convenience Method for updating a Categories color
     *
     * @param category Kategorie mit geänderten Werten.
     * @return True bei erfolg, false bei Fehlschlag.
     */
    public boolean updateCategoryName(Category category) {

        ContentValues updatedCategory = new ContentValues();
        updatedCategory.put(ExpensesDbHelper.CATEGORIES_COL_NAME, category.getName());
        updatedCategory.put(ExpensesDbHelper.CATEGORIES_COL_COLOR, category.getColor());
        updatedCategory.put(ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE, category.getDefaultExpenseType());

        int affectedRRows = database.update(ExpensesDbHelper.TABLE_CATEGORIES, updatedCategory, ExpensesDbHelper.CATEGORIES_COL_ID + " = ?", new String[]{category.getIndex() + ""});
        return affectedRRows == 1;
    }

    /**
     * Convenience Method for deleting a Category by id
     *
     * @param categoryId Id der Kategorie die gelöscht werden soll
     * @return Das Ergebnis der Operation
     * @throws CannotDeleteCategoryException Wenn es noch Buchungen mit dieser Kategorie gibt, dann kann die Kategorie nicht gelöscht werden
     */
    public int deleteCategory(long categoryId) throws CannotDeleteCategoryException {

        if (hasCategoryBookings(categoryId))
            throw new CannotDeleteCategoryException("Category with existing Bookings cannot be deleted");

        Log.d(TAG, "delete Category + " + categoryId);
        return database.delete(ExpensesDbHelper.TABLE_CATEGORIES, ExpensesDbHelper.CATEGORIES_COL_ID + " = ?", new String[]{"" + categoryId});
    }

    /**
     * Methode um herauszufinde ob es Buchungen mit dieser Kategorie gibt
     *
     * @param categoryId Id der zu checkenden Kategorie
     * @return boolean
     */
    private boolean hasCategoryBookings(long categoryId) {

        return isEntityAssignedToBooking(categoryId, ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID);
    }


    public long createTemplateBooking(long bookingId) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID, bookingId);
        Log.d(TAG, "creating template booking");

        return database.insert(ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS, null, values);
    }

    public ArrayList<ExpenseObject> getTemplates() {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID
                + " FROM " + ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS + ";";
        Log.d(TAG, "getTemplates: " + selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getTemplates: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        ArrayList<ExpenseObject> allTemplates = new ArrayList<>();
        while (!c.isAfterLast()) {

            long index = c.getLong(c.getColumnIndex(ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID));
            allTemplates.add(getBookingById(index));

            c.moveToNext();
        }

        c.close();
        return allTemplates;
    }

    @Nullable
    public ExpenseObject getTemplate(long templateId) {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID
                + " FROM " + ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID + " = " + templateId;
        Log.d(TAG, "getTemplate: " + selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getTemplate: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        return getBookingById(c.getLong(c.getColumnIndex(ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID)));
    }

    /**
     * Methode um Templates zu updaten.
     *
     * @param templateId Template welches geändert werden soll
     * @param bookingId  Buchung die das neue Template werden soll
     * @return True bei erfolg, false bei Fehlschlag
     */
    public boolean updateTemplate(long templateId, long bookingId) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID, bookingId);

        int affectedRows = database.update(ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS, values, ExpensesDbHelper.TEMPLATE_COL_ID + " = ?", new String[]{"" + templateId});
        return affectedRows == 1;
    }

    public int deleteTemplate(long templateId) {

        Log.d(TAG, "deleting template: " + templateId);
        return database.delete(ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS, ExpensesDbHelper.TEMPLATE_COL_ID + " = ?", new String[]{"" + templateId});
    }


    public long createRecurringBooking(long recurringBookingId, long startTimeInMills, int frequency, long endTimeInMills) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID, recurringBookingId);
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_START, startTimeInMills);
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY, frequency);
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_END, endTimeInMills);
        Log.d(TAG, "creating recurring booking: " + recurringBookingId);

        return database.insert(ExpensesDbHelper.TABLE_RECURRING_BOOKINGS, null, values);
    }

    public ArrayList<ExpenseObject> getRecurringBookings(Calendar dateRngStart, Calendar endDate) {//TODO nicht ganz zufrieden mit der funktion, bitte überdenken

        //exclude all events which end before the given date range
        String selectQuery = "SELECT "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY
                + " FROM " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
                + " WHERE " + ExpensesDbHelper.RECURRING_BOOKINGS_COL_END + " > " + endDate.getTimeInMillis() + ";";
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getRecurringBookings: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        ExpenseObject expense;
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        ArrayList<ExpenseObject> allRecurringBookings = new ArrayList<>();
        while (!c.isAfterLast()) {

            //get start date of recurring booking
            String startDateString = c.getString(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_START));
            start.setTimeInMillis(Long.parseLong(startDateString));

            //get end date of recurring booking
            String endDateString = c.getString(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_END));
            end.setTimeInMillis(Long.parseLong(endDateString));

            //get frequency
            int frequency = c.getInt(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY));

            //get the time difference between the last occurrence and the start of the date range in hours
            int temp = 0 - ((int) ((start.getTimeInMillis() - dateRngStart.getTimeInMillis()) / 3600000) % frequency);

            //set start date of recurring event to the first date of the date range
            start.setTimeInMillis(dateRngStart.getTimeInMillis());

            //set start date of recurring event to the last occurrence of the event
            start.add(Calendar.HOUR, temp);

            //as long as the start date is before the end of its cycle and also before the end of the given date range
            while (start.before(end) && start.before(endDate)) {

                if (start.after(dateRngStart)) {

                    expense = getBookingById(c.getLong(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID)));
                    expense.setDateTime(start);
                    allRecurringBookings.add(expense);
                }

                start.add(Calendar.HOUR, frequency);
            }
            c.moveToNext();
        }

        c.close();
        return allRecurringBookings;
    }

    @Nullable
    public ExpenseObject getRecurringBooking(long recurringId) {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID
                + " FROM " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
                + " WHERE " + ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + " = " + recurringId;
        Log.d(TAG, "getRecurringBooking: " + selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getRecurringBooking: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        long bookingId = c.getLong(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID));
        c.close();

        return getBookingById(bookingId);
    }


    /**
     * Methode um Wiederkehrende Buchungen zu updaten.
     *
     * @param newRecurringBooking Wiederkehrende Buchung mit geänderten Werten
     * @param startDateInMills    Neuer Startzeitpunkz
     * @param frequency           Neue Häufigkeit
     * @param endDateInMills      Neuer Endzeitpunkt
     * @param recurringId         Id der anzupassenden Buchung
     * @return True bei erfolg, False bei Fehlschlag.
     */
    public boolean updateRecurringBooking(ExpenseObject newRecurringBooking, long startDateInMills, int frequency, long endDateInMills, long recurringId) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID, newRecurringBooking.getIndex());
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_START, startDateInMills);
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY, frequency);
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_END, endDateInMills);
        Log.d(TAG, "updateRecurringBooking: " + recurringId);

        int affectedRows = database.update(ExpensesDbHelper.TABLE_RECURRING_BOOKINGS, values, ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + " = ?", new String[]{"" + recurringId});
        return affectedRows == 1;
    }

    public int deleteRecurringBooking(long index) {

        Log.d(TAG, "deleteRecurringBooking: " + index);
        return database.delete(ExpensesDbHelper.TABLE_RECURRING_BOOKINGS, ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + "= ?", new String[]{"" + index});
    }


    public Currency createCurrency(Currency currency) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.CURRENCIES_COL_NAME, currency.getName());
        values.put(ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME, currency.getShortName());
        values.put(ExpensesDbHelper.CURRENCIES_COL_SYMBOL, currency.getSymbol());

        long index = database.insert(ExpensesDbHelper.TABLE_CURRENCIES, null, values);

        return new Currency(index, currency.getName(), currency.getShortName(), currency.getSymbol(), currency.getRateToBase());
    }

    @NonNull
    public Currency getCurrency(long currencyId) {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CURRENCIES_COL_ID + ", "
                + ExpensesDbHelper.CURRENCIES_COL_NAME + ", "
                + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + ", "
                + ExpensesDbHelper.CURRENCIES_COL_SYMBOL
                + " FROM " + ExpensesDbHelper.TABLE_CURRENCIES
                + " WHERE " + ExpensesDbHelper.CURRENCIES_COL_ID + " = " + currencyId + ";";
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getExpenseCurrency: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        return cursorToCurrency(c);
    }

    @Nullable
    public Currency getCurrency(String shortName) {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CURRENCIES_COL_ID + ", "
                + ExpensesDbHelper.CURRENCIES_COL_NAME + ", "
                + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + ", "
                + ExpensesDbHelper.CURRENCIES_COL_SYMBOL
                + " FROM " + ExpensesDbHelper.TABLE_CURRENCIES
                + " WHERE " + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + " = '" + shortName + "';";
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getExpenseCurrency: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        return c.isAfterLast() ? null : cursorToCurrency(c);
    }

    public ArrayList<Currency> getAllCurrencies() {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CURRENCIES_COL_ID + ", "
                + ExpensesDbHelper.CURRENCIES_COL_NAME + ", "
                + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + ", "
                + ExpensesDbHelper.CURRENCIES_COL_SYMBOL
                + " FROM " + ExpensesDbHelper.TABLE_CURRENCIES + ";";
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getAllCurrencies: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        ArrayList<Currency> currencies = new ArrayList<>();
        while (!c.isAfterLast()) {

            currencies.add(cursorToCurrency(c));
            c.moveToNext();
        }

        return currencies;
    }

    private Long getCurrencyId(String curShortName) throws EntityNotExistingException {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CURRENCIES_COL_ID
                + " FROM " + ExpensesDbHelper.TABLE_CURRENCIES
                + " WHERE " + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + " = '" + curShortName + "';";
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getCurrencyId: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        assertCursorNotEmpty(c);
        long currencyId = c.getLong(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_ID));
        c.close();

        return currencyId;
    }

    /**
     * Methode um zu überprüfen, dass etwas mit einer Query gefunden wurde
     *
     * @param cursor Cursor
     * @throws EntityNotExistingException Wenn in dem Cursor keine Daten enthalten sind wird ein Fehler ausgelöst
     */
    private void assertCursorNotEmpty(Cursor cursor) throws EntityNotExistingException {

        //todo wann immer etwas aus der datenbank abgefragt wird soll der cursor vor der weiterverarbeitung mit dieser funktion gepüft werden
        if (cursor == null)
            throw new EntityNotExistingException("No entity in database");
    }

    /**
     * Methode um die Werte einer Wärhung anzupassen
     *
     * @param currency Währung mit angepassten Werten
     * @return True bei Erfolg, false bei Fehlschlag
     */
    public boolean updateCurrency(Currency currency) {

        ContentValues updatedCurrency = new ContentValues();
        updatedCurrency.put(ExpensesDbHelper.CURRENCIES_COL_SYMBOL, currency.getSymbol());
        updatedCurrency.put(ExpensesDbHelper.CURRENCIES_COL_NAME, currency.getName());
        updatedCurrency.put(ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME, currency.getShortName());

        int affectedRows = database.update(ExpensesDbHelper.TABLE_CURRENCIES, updatedCurrency, ExpensesDbHelper.CURRENCIES_COL_ID + " = ?", new String[]{currency.getIndex() + ""});
        return affectedRows == 1;
    }

    /**
     * Methode um eine Währung zu löschen
     *
     * @param currencyId Id der zu löschenden Währung
     * @return Status der operation
     * @throws CannotDeleteCurrencyException Eine Währung kann nicht gelöscht werden, wenn es noch Konten oder Buchungen mit dieser Währung gibt
     */
    public int deleteCurrency(long currencyId) throws CannotDeleteCurrencyException {

        if (hasCurrencyAccounts(currencyId) || hasCurrencyBookings(currencyId))
            throw new CannotDeleteCurrencyException("Cannot delete Currency while there are still bookings or accounts with this currency");

        Log.d(TAG, "deleteCurrency at index: " + currencyId);
        return database.delete(ExpensesDbHelper.TABLE_CURRENCIES, ExpensesDbHelper.CURRENCIES_COL_ID + " = ?", new String[]{"" + currencyId});
    }

    /**
     * Methode um zu überprüfen ob es eine Buchung gibt, die dieser Währung zugeordnet ist
     *
     * @param currencyId Id der zu überprüfenden Währung
     * @return boolean
     */
    private boolean hasCurrencyBookings(long currencyId) {

        return isEntityAssignedToBooking(currencyId, ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID);
    }

    /**
     * Methode um zu überprüfen ob es ein Konto gibt, welches diese benutzt
     *
     * @param currencyId Id der zu überprüfenden Währung
     * @return booelean
     */
    private boolean hasCurrencyAccounts(long currencyId) {

        //todo neuen namen für die methode finden!
        //TODO funktionalität implementieren
        return false;
    }


    /**
     * Method for creating a new exchange rate based on currency objects
     *
     * @param fromCur     getTitle of first currency
     * @param toCur       getTitle of second currency
     * @param rate        exchange rate from currency one to currency two
     * @param dateInMills fetching date in milliseconds
     * @return state of operation
     */
    public long createExchangeRate(Currency fromCur, Currency toCur, double rate, long dateInMills) throws EntityNotExistingException {

        return createExchangeRate(fromCur.getIndex(), toCur.getIndex(), rate, dateInMills);
    }

    /**
     * Method for creating an exchange rate based on currency names
     *
     * @param fromCur     from currency object
     * @param toCur       to currency object
     * @param rate        exchange rate from currency on to curency two
     * @param dateInMills fetching date in milliseconds
     * @return state of operation
     */
    public long createExchangeRate(String fromCur, String toCur, double rate, long dateInMills) throws EntityNotExistingException {

        return createExchangeRate(getCurrencyId(fromCur), getCurrencyId(toCur), rate, dateInMills);
    }

    /**
     * Method for creating an exchange rate between to currencies
     *
     * @param fromCurIndex id of first currency
     * @param toCurIndex   id of second currency
     * @param rate         exchange rate from currency on to currency two
     * @param dateInMills  fetching date in milliseconds
     * @return state of operation
     */
    public long createExchangeRate(long fromCurIndex, long toCurIndex, double rate, long dateInMills) throws EntityNotExistingException {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_FROM_CURRENCY_ID, fromCurIndex);
        values.put(ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_TO_CURRENCY_ID, toCurIndex);
        values.put(ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_EXCHANGE_RATE, rate);
        values.put(ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_SERVER_DATE, dateInMills);

        try {
            return database.insertOrThrow(ExpensesDbHelper.TABLE_CURRENCY_EXCHANGE_RATES, null, values);
        } catch (SQLException e) {

            throw new EntityNotExistingException(e.getMessage());
        }
    }

    /**
     * Returns the Exchange rate between two currencies with additional date information
     *
     * @param fromCurIndex Database index of first Currency
     * @param toCurIndex   Database index of second Currency
     * @param timeInMills  parameter which specifies the date of the ExchangeRate in Milliseconds
     * @return HashMap(ExchangeRate, Download date of fetched exchange rate)
     */
    @Nullable
    @SuppressWarnings("UnnecessaryLocalVariable")
    public HashMap<Double, Long> getExtendedExchangeRate(long fromCurIndex, long toCurIndex, long timeInMills) {

        HashMap<Double, Long> extendedExchangeRateInfo = new HashMap<>();
        Calendar currentDay = Calendar.getInstance();
        currentDay.setTimeInMillis(timeInMills);
        Calendar dayStart = currentDay;
        dayStart.set(Calendar.HOUR_OF_DAY, 0);
        dayStart.set(Calendar.MINUTE, 0);
        dayStart.set(Calendar.SECOND, 1);
        Calendar dayEnd = currentDay;
        dayEnd.set(Calendar.HOUR_OF_DAY, 23);
        dayEnd.set(Calendar.MINUTE, 59);
        dayEnd.set(Calendar.SECOND, 59);

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_EXCHANGE_RATE + ", "
                + ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_SERVER_DATE
                + " FROM " + ExpensesDbHelper.TABLE_CURRENCY_EXCHANGE_RATES
                + " WHERE "
                + "(" + ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_FROM_CURRENCY_ID + " = " + fromCurIndex
                + " OR " + ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_FROM_CURRENCY_ID + " = " + toCurIndex + ")"
                + " AND "
                + "(" + ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_TO_CURRENCY_ID + " = " + toCurIndex
                + " OR " + ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_TO_CURRENCY_ID + " = " + fromCurIndex + ")"
                + " AND " + ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_SERVER_DATE + " > " + dayStart.getTimeInMillis()
                + " AND " + ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_SERVER_DATE + " < " + dayEnd.getTimeInMillis()
                + " ORDER BY " + ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_TIMESTAMP + " DESC;";
        Log.d(TAG, "getExchangeRate: " + selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getExtendedExchangeRate: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        if (!c.isAfterLast()) {

            Double exchangeRate = c.getDouble(c.getColumnIndex(ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_EXCHANGE_RATE));
            long serverDate = c.getLong(c.getColumnIndex(ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_SERVER_DATE));
            extendedExchangeRateInfo.put(exchangeRate, serverDate);
            c.close();

            return extendedExchangeRateInfo;
        } else {

            Log.w(TAG, "getRateToBase: No available ExchangeRate from " + fromCurIndex + " to " + toCurIndex + " in mDatabase!");
            c.close();
            return null;
        }
    }

    /**
     * Method for recieving ExchangeRates between two Currencies
     *
     * @param fromCurIndex Id of first Currency
     * @param toCurIndex   Id of second Currency
     * @param timeInMills  parameter which specifies the date of the ExchangeRate in Milliseconds
     * @return The ExchangeRate if available null if not
     */
    @Nullable
    public Double getExchangeRate(long fromCurIndex, long toCurIndex, long timeInMills) {

        HashMap<Double, Long> extendedExchangeRate = getExtendedExchangeRate(fromCurIndex, toCurIndex, timeInMills);

        if (extendedExchangeRate != null) {

            Map.Entry<Double, Long> entry = extendedExchangeRate.entrySet().iterator().next();
            return entry.getKey();
        } else {

            return null;
        }
    }

    /**
     * Method for getting an exchange rate from rate to base to toCurIndex
     *
     * @param fromCurIndex id of convert to currency
     * @param timeInMills  parameter which specifies the date of the ExchangeRate in Milliseconds
     * @return rate to base if available else null
     */
    @Nullable
    public Double getRateToBase(long fromCurIndex, long timeInMills) {

        SharedPreferences preferences = mContext.getSharedPreferences("UserSettings", Context.MODE_PRIVATE);
        long baseCurIndex = preferences.getLong("mainCurrencyIndex", 0);

        HashMap<Double, Long> extendedExchangeRate = getExtendedExchangeRate(fromCurIndex, baseCurIndex, timeInMills);

        if (extendedExchangeRate != null) {

            Map.Entry<Double, Long> entry = extendedExchangeRate.entrySet().iterator().next();
            return entry.getKey();
        } else {

            return null;
        }
    }

    /**
     * Method for deleting an exchange rate
     *
     * @param exchangeRateId id of exchange rate
     * @return state of operation
     */
    public int deleteExchangeRate(long exchangeRateId) {

        Log.d(TAG, "deleteExchangeRate index: " + exchangeRateId);
        return database.delete(ExpensesDbHelper.TABLE_CURRENCY_EXCHANGE_RATES, ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_ID + " = ?", new String[]{"" + exchangeRateId});
    }


    /**
     * Method for creating an Expense which has to be converted
     *
     * @param expense Expense
     */
    public void insertConvertExpense(ExpenseObject expense) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.CONVERT_EXPENSES_STACK_COL_BOOKING, expense.getIndex());
        values.put(ExpensesDbHelper.CONVERT_EXPENSES_STACK_COL_LATEST_TRY, expense.getDateTime().getTimeInMillis());

        database.insert(ExpensesDbHelper.TABLE_CONVERT_EXPENSES_STACK, null, values);
    }

    /**
     * Method for recieving all expenses that have to be converted
     *
     * @return list of expenses
     */
    public ArrayList<ExpenseObject> convertExpenses() {

        ArrayList<ExpenseObject> convertExpenses = new ArrayList<>();

        String selectQuery;
        selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_CONVERT_EXPENSES_STACK + ";";
        Log.d(TAG, "convertExpenses: " + selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {

            convertExpenses.add(cursorToExpense(c));
            c.moveToNext();
        }

        return convertExpenses;
    }

    /**
     * Method for updating the latest convert try
     *
     * @param index id of expense
     * @param rate  new rate
     */
    public void updateExpenseExchangeRate(long index, double rate) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.BOOKINGS_COL_EXCHANGE_RATE, rate);

        database.update(ExpensesDbHelper.TABLE_BOOKINGS, values, ExpensesDbHelper.BOOKINGS_COL_ID + " = ?", new String[]{"" + index});
    }

    /**
     * Method for removing the expense from the stack after the ConvertService converted the expense succesfully
     *
     * @param index ind of expense
     */
    public void deleteConvertExpense(long index) {

        database.delete(ExpensesDbHelper.TABLE_CONVERT_EXPENSES_STACK, ExpensesDbHelper.CONVERT_EXPENSES_STACK_COL_BOOKING + " = ?", new String[]{"" + index});
    }

    /**
     * Methode um zu überprüfen ob zu einer bestimmten Entity noch mindestends eine Buchunge zugeordnet ist
     *
     * @param propertyId     Id der Entity
     * @param propertyColumn Spaltenname der Entity in der Bookings tabelle
     * @return boolean
     */
    private boolean isEntityAssignedToBooking(long propertyId, String propertyColumn) {

        String selectQuery;

        //prüfe die Bookings tabelle
        selectQuery = "SELECT"
                + " COUNT(1) 'exists'"
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " WHERE " + propertyColumn + " = " + propertyId + ";";
        Log.d(TAG, "isChild: " + selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        c.moveToFirst();

        if (c.getInt(c.getColumnIndex("exists")) != 0) {

            c.close();
            return true;
        }

        //prüfe die ChilBookings tabelle
        selectQuery = "SELECT"
                + " COUNT(1) 'exists'"
                + " FROM " + ExpensesDbHelper.TABLE_CHILD_BOOKINGS
                + " WHERE " + propertyColumn + " = " + propertyId + ";";
        Log.d(TAG, "isChild: " + selectQuery);

        c = database.rawQuery(selectQuery, null);
        c.moveToFirst();

        if (c.getInt(c.getColumnIndex("exists")) != 0) {

            c.close();
            return true;
        } else {

            c.close();
            return false;
        }
    }
}