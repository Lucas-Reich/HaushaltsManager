package com.example.lucas.haushaltsmanager.Database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Tag;
import com.example.lucas.haushaltsmanager.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExpensesDataSource {

    private static final String TAG = ExpensesDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private ExpensesDbHelper dbHelper;
    private Context mContext;
    private Calendar CAL;

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

        CAL = Calendar.getInstance();

        int expenseId = c.getInt(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_ID));
        String date = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_DATE));
        String title = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_TITLE));
        double price = c.getDouble(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_PRICE));
        boolean expenditure = c.getInt(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE)) == 1;
        String notice = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_NOTICE));
        int exchangeRate = c.getInt(c.getColumnIndex(ExpensesDbHelper.CHILD_BOOKINGS_COL_EXCHANGE_RATE));

        long categoryId = c.getLong(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID));
        String categoryName = c.getString(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_NAME));
        String categoryColor = c.getString(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_COLOR));
        boolean defaultExpenseType = c.getInt(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_EXPENSE_TYPE)) == 1;
        Category category = new Category(categoryId, categoryName, categoryColor, defaultExpenseType);

        long curId = c.getLong(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID));
        String curName = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_NAME));
        String curShortName = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME));
        String curSymbol = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_SYMBOL));
        Double rateToBase = getRateToBase(curId, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(CAL.getTime()));
        Currency currency = new Currency(curId, curName, curShortName, curSymbol, rateToBase);

        long accountId = c.getLong(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID));
        String accountName = c.getString(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_NAME));
        int accountBalance = c.getInt(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_BALANCE));
        Account account = new Account(accountId, accountName, accountBalance, currency);

        long currencyId = c.getLong(c.getColumnIndex(ExpensesDbHelper.CHILD_BOOKINGS_COL_CURRENCY_ID));
        Currency expenseCurrency = getCurrency(currencyId);
        expenseCurrency.setRateToBase(exchangeRate);


/*TODO das expenseobject sollte auch das tag object benutzen
        expense.setTags(getTagsToBooking(expenseId));
*/

        return new ExpenseObject(expenseId, title, price, expenditure, date, category, notice, account, expenseCurrency);
    }

    /**
     * Convenience Method for mapping a Cursor to a Booking
     *
     * @param c Cursor object obtained by a SQLITE search query
     * @return An remapped ExpenseObject
     */
    private ExpenseObject cursorToExpense(Cursor c) {

        CAL = Calendar.getInstance();

        int expenseId = c.getInt(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_ID));

        Log.d(TAG, "cursorToExpense: " + expenseId);

        String date = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_DATE));

        String title = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_TITLE));

        double price = c.getDouble(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_PRICE));

        boolean expenditure = c.getInt(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE)) == 1;

        String notice = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_NOTICE));

        long categoryId = c.getLong(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID));
        String categoryName = c.getString(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_NAME));
        String categoryColor = c.getString(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_COLOR));
        boolean defaultExpenseType = c.getInt(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_EXPENSE_TYPE)) == 1;
        Category category = new Category(categoryId, categoryName, categoryColor, defaultExpenseType);

        long curId = c.getLong(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID));
        String curName = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_NAME));
        String curShortName = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME));
        String curSymbol = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_SYMBOL));
        Double rateToBase = getRateToBase(curId, date.substring(0, 10));
        Currency currency = new Currency(curId, curName, curShortName, curSymbol, rateToBase);

        long accountId = c.getLong(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID));
        String accountName = c.getString(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_NAME));
        int accountBalance = c.getInt(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_BALANCE));
        Account account = new Account(accountId, accountName, accountBalance, currency);

        int exchangeRate = c.getInt(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_EXCHANGE_RATE));

        long currencyId = c.getLong(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID));
        Currency expenseCurrency = getCurrency(currencyId);
        expenseCurrency.setRateToBase(exchangeRate);

/*TODO das expenseobject sollte auch das tag object benutzen
        expense.setTags(getTagsToBooking(expenseId));
*/

        ExpenseObject expense = new ExpenseObject(expenseId, title, price, expenditure, date, category, notice, account, expenseCurrency);

        boolean isParent = c.getInt(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_IS_PARENT)) == 1;
        if (isParent) {

            Log.d(TAG, "cursorToExpense: " + expenseId);
            expense.addChildren(getChildrenToParent(expenseId));
        }

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
        boolean defaultExpenseType = c.getInt(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_EXPENSE_TYPE)) == 1;

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
        int accountBalance = c.getInt(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_BALANCE));

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
     * creates an dummy expense for combining expenses
     *
     * @return id of expense
     */
    private long createDummyExpense() {

        SharedPreferences preferences = mContext.getSharedPreferences("UserSettings", Context.MODE_PRIVATE);
        Currency currency = getCurrency(preferences.getLong("mainCurrencyIndex", 1));

        ExpenseObject dummyExpense;

        Account account = new Account(9999, "", 0, currency);

        dummyExpense = new ExpenseObject(mContext.getResources().getString(R.string.no_name), 0, true, Category.createDummyCategory(mContext), null, account);
        dummyExpense.setDateTime(Calendar.getInstance());

        return createBooking(dummyExpense);
    }


    /**
     * Convenience Method for creating a new Account
     *
     * @param account Account object  which should be created
     * @return the id of the created tag. -1 if the insertion failed
     */
    public long createAccount(Account account) {

        if (account.getCurrency().getIndex() == -1)
            throw new RuntimeException("Cannot create account with dummy Currency object!");

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.ACCOUNTS_COL_NAME, account.getAccountName());
        values.put(ExpensesDbHelper.ACCOUNTS_COL_BALANCE, account.getBalance());
        values.put(ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID, account.getCurrency().getIndex());

        Log.d(TAG, "Creating account: " + account.getAccountName());
        return database.insert(ExpensesDbHelper.TABLE_ACCOUNTS, null, values);
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
     * Method for getting an account by its name
     *
     * @param accountName name of account
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

    public long updateAccount(Account account) {

        throw new UnsupportedOperationException("Updating Accounts is not Supported");//todo
    }

    /**
     * Convenience Method for deleting a Account
     *
     * @param accountId Account object which should be deleted
     * @return the number of affected rows
     */
    public int deleteAccount(long accountId) {

        if (hasAccountBookings(accountId))
            throw new RuntimeException("Account with existing bookings cannot be deleted!");

        Log.d(TAG, "Deleting account at index: " + accountId);
        return database.delete(ExpensesDbHelper.TABLE_ACCOUNTS, ExpensesDbHelper.ACCOUNTS_COL_ID + " = ?", new String[]{"" + accountId});
    }

    /**
     * Methode um zu checken ob noch mindestends eine Buchung mit diesem Konto existiert
     *
     * @param accountId Id des Kontos
     * @return Boolean
     */
    public boolean hasAccountBookings(long accountId) {

        String selectQuery;

        //check bookings table
        selectQuery = "SELECT"
                + " COUNT(1) 'exists'"
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID + " = " + accountId + ";";
        Log.d(TAG, "isChild: " + selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        c.moveToFirst();

        if (c.getInt(c.getColumnIndex("exists")) != 0) {

            return true;
        }

        //check childBookings table
        selectQuery = "SELECT"
                + " COUNT(1) 'exists'"
                + " FROM " + ExpensesDbHelper.TABLE_CHILD_BOOKINGS
                + " WHERE " + ExpensesDbHelper.CHILD_BOOKINGS_COL_ACCOUNT_ID + " = " + accountId + ";";
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


    /**
     * Convenience Method for creating a new Tag
     *
     * @param tagName name of the tag which should be created
     * @return the id of the created tag. -1 if the insertion failed
     */
    public long createTag(String tagName) {


        //TODO create tag wenn es noch nicht existiert
        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.TAGS_COL_NAME, tagName);

        Log.d(TAG, "created tag: " + tagName);
        return database.insert(ExpensesDbHelper.TABLE_TAGS, null, values);
    }

    /**
     * Convenience Method for getting an specific Tag
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
                + ExpensesDbHelper.TAGS_COL_NAME + ", "
                + " FROM " + ExpensesDbHelper.TABLE_TAGS;
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

    public long updateTag(Tag tag) {

        throw new UnsupportedOperationException("Updating Tags is not Supported");//todo
    }

    /**
     * Convenience Method for deleting a Tag
     *
     * @param tagId the id of the entry which should be deleted
     * @return the number of affected rows
     */
    public int deleteTag(long tagId) {

        //TODO ein tag kann nicht gelöscht werden, wenn es noch einer buchung zugeordnet ist
        Log.d(TAG, "deleted tag at index " + tagId);
        return database.delete(ExpensesDbHelper.TABLE_TAGS, ExpensesDbHelper.TAGS_COL_ID + " = ?", new String[]{"" + tagId});
    }


    /**
     * Class internal Method for assigning a Tag to a Booking
     *
     * @param bookingId Id of the booking where the id has to be assigned to
     * @param tagId     Id of the Tag which should be assigned to the booking
     * @return the index of the inserted row
     */
    private long assignTagToBooking(long bookingId, long tagId) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_ID, tagId);
        values.put(ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_ID, bookingId);

        Log.d(TAG, "assigning tag " + tagId + " to booking " + bookingId);
        return database.insert(ExpensesDbHelper.TABLE_BOOKINGS_TAGS, null, values);
    }

    /**
     * Class internal Method for requesting all Tags to a Booking by the BookingId
     *
     * @param bookingId The id of the Booking where the Tags should be outputted
     * @return All Tags to the specified booking in an String[]
     */
    private List<Tag> getTagsToBooking(long bookingId) {

        String selectQuery;
        selectQuery = "SELECT "
                + ExpensesDbHelper.TABLE_TAGS + "." + ExpensesDbHelper.TAGS_COL_ID + ", "
                + ExpensesDbHelper.TABLE_TAGS + "." + ExpensesDbHelper.TAGS_COL_NAME + ", "
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
     * @param tagId Id of the Tag where all Bookings are requested
     * @return All ids of the affected Bookings
     */
    private List<ExpenseObject> getBookingsToTag(long tagId) {

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

        for (ExpenseObject booking : bookings) {

            createBooking(booking);
        }
    }

    /**
     * Convenience Method for creating a Booking
     *
     * @param expense The expense which has to be stored in the DB
     * @return Id of the created Booking
     */
    public long createBooking(ExpenseObject expense) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.BOOKINGS_COL_PRICE, expense.getUnsignedPrice());
        values.put(ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID, expense.getCategory().getIndex());
        values.put(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE, expense.getExpenditure());
        values.put(ExpensesDbHelper.BOOKINGS_COL_TITLE, expense.getTitle());
        values.put(ExpensesDbHelper.BOOKINGS_COL_DATE, expense.getDBDateTime());
        values.put(ExpensesDbHelper.BOOKINGS_COL_NOTICE, expense.getNotice());
        values.put(ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID, expense.getAccount().getIndex());
        values.put(ExpensesDbHelper.BOOKINGS_COL_EXCHANGE_RATE, expense.getExchangeRate());
        if (expense.hasChildren() || expense.getAccount().getIndex() == 9999) {

            values.put(ExpensesDbHelper.BOOKINGS_COL_IS_PARENT, true);
        } else {

            values.put(ExpensesDbHelper.BOOKINGS_COL_IS_PARENT, false);
        }
        values.put(ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID, expense.getExpenseCurrency().getIndex());

        long bookingId = database.insert(ExpensesDbHelper.TABLE_BOOKINGS, null, values);
        Log.d(TAG, "created expense at index: " + bookingId);

        /*TODO die tag funktinalität noch einmal überdenken.. am besten sollte ich eine tag klasse erstellen und dann die tags per id zu den bookings hinzufügen
        //assign all chosen tags to the booking
        for (String tag : expense.getTags()) {

            assignTagToBooking(bookingId, tag);
        }
        */

        return bookingId;
    }

    /**
     * Convenience Method for getting a Booking
     *
     * @param bookingId Get the Booking values to the selected id
     * @return The requested expense
     */
    public ExpenseObject getBookingById(long bookingId) {

        String selectQuery;
        selectQuery = "SELECT "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ID + ", "
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
                + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_EXPENSE_TYPE + ", "
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

        return getBookings(null, null);
    }

    /**
     * Method for receiving all bookings in a specified date range
     *
     * @param startDate startind date
     * @param endDate   ending date
     * @return list of Expenses which are between the starting and end date
     */
    public ArrayList<ExpenseObject> getBookings(String startDate, String endDate) {

        String selectQuery;
        selectQuery = "SELECT "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ID + ", "
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
                + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_EXPENSE_TYPE + ", "
                + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " LEFT JOIN " + ExpensesDbHelper.TABLE_CATEGORIES + " ON " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID + " = " + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_ID
                + " LEFT JOIN " + ExpensesDbHelper.TABLE_ACCOUNTS + " ON " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID + " = " + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_ID
                + " LEFT JOIN " + ExpensesDbHelper.TABLE_CURRENCIES + " ON " + ExpensesDbHelper.TABLE_ACCOUNTS + "." + ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID + " = " + ExpensesDbHelper.TABLE_CURRENCIES + "." + ExpensesDbHelper.CURRENCIES_COL_ID;
        if (startDate != null && endDate != null) {

            selectQuery += " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_DATE + " BETWEEN '" + startDate + "' AND '" + endDate + "'";
        }
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

    public int updateBooking(ExpenseObject expense) {

        throw new UnsupportedOperationException("Updating bookings is not supported");//todo
    }

    /**
     * Method for deleting multiple bookings at once
     *
     * @param bookingIds array of booking ids
     * @return number of affected rows
     */
    public int deleteBookings(long bookingIds[]) {

        int affectedRows = 0;

        for (long bookingId : bookingIds) {

            deleteBooking(bookingId);
            affectedRows++;
        }

        return affectedRows;
    }

    /**
     * Convenience Method for deleting a Booking
     *
     * @param bookingId Id of the Booking which should be deleted
     * @return the number of affected rows
     */
    public int deleteBooking(long bookingId) {


        Log.d(TAG, "deleteBooking: deleting booking " + bookingId);

        removeTagsFromBooking(bookingId);
        deleteChildrenFromParent(bookingId);
        return database.delete(ExpensesDbHelper.TABLE_BOOKINGS, ExpensesDbHelper.BOOKINGS_COL_ID + " = ?", new String[]{"" + bookingId});
    }


    /**
     * Function to add child to parentId
     *
     * @param child    child to append to parent
     * @param parentId Id of parent booking
     * @return index of inserted child
     */
    private long addChild(ExpenseObject child, long parentId) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_PRICE, child.getUnsignedPrice());
        values.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_PARENT_BOOKING_ID, parentId);
        values.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_CATEGORY_ID, child.getCategory().getIndex());
        values.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_EXPENDITURE, child.getExpenditure());
        values.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_TITLE, child.getTitle());
        values.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_DATE, child.getDBDateTime());
        values.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_NOTICE, child.getNotice());
        values.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_ACCOUNT_ID, child.getAccount().getIndex());
        values.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_EXCHANGE_RATE, child.getExchangeRate());
        values.put(ExpensesDbHelper.CHILD_BOOKINGS_COL_CURRENCY_ID, child.getExpenseCurrency().getIndex());

        long childId = database.insert(ExpensesDbHelper.TABLE_CHILD_BOOKINGS, null, values);
        Log.d(TAG, "created child expense at index: " + childId);

/*TODO siehe createBooking
        for (String tag : child.getTags()) {

            assignTagToBooking(childId, tag);
        }
*/
        return childId;
    }

    /**
     * Functions ensures that the parent expense is no child itself.
     * It also checks whether the parent expense has children attached to it or not.
     * If the parent expense has no children then a dummy expense is created and the parent and the child are attached to it.
     *
     * @param childExpense expense to add to the parent
     * @param parentId     ID of parent expense
     * @return ID of parent
     */
    public long addChildToBooking(ExpenseObject childExpense, long parentId) {

        if (!isChild(parentId)) {

            ExpenseObject parent = getBookingById(parentId);

            if (parent.hasChildren()) {

                addChild(childExpense, parentId);

                return parentId;
            } else {

                parentId = createDummyExpense();
                addChild(parent, parentId);
                deleteBooking(parent.getIndex());
                addChild(childExpense, parentId);

                return parentId;
            }
        } else {

            Log.w(TAG, "createChildBooking: Error while adding child to Parent! Parent is Child");
            throw new UnsupportedOperationException("Cannot add Booking to an child Booking");
            //return -1;
        }
    }

    public ExpenseObject createChildBooking(List<ExpenseObject> children) {

        long parentId = createDummyExpense();

        for (ExpenseObject child : children) {

            addChildToBooking(child, parentId);
            deleteBooking(child.getIndex());
        }

        return getBookingById(parentId);
    }

    public ArrayList<ExpenseObject> getChildrenToParent(long parentId) {

        String selectQuery;
        selectQuery = "SELECT "
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
                + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_EXPENSE_TYPE + ", "
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

    public ExpenseObject getChildBookingById(long childId) {

        String selectQuery;
        selectQuery = "SELECT "
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
                + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_EXPENSE_TYPE + ", "
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

    public boolean isChild(long expenseId) {

        String selectQuery;

        selectQuery = "SELECT"
                + " COUNT(1) 'exists'"
                + " FROM " + ExpensesDbHelper.TABLE_CHILD_BOOKINGS
                + " WHERE " + ExpensesDbHelper.CHILD_BOOKINGS_COL_ID + " = " + expenseId + ";";
        Log.d(TAG, "isChild: " + selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        c.moveToFirst();


        return c.getInt(c.getColumnIndex("exists")) != 0;
    }

    public int updateChildBooking(ExpenseObject expense) {

        throw new UnsupportedOperationException("Updating children is not Supported");//todo
    }

    public int deleteChildBooking(long childId) {

        //TODO wenn das kind das letzte des parents war muss der parent wieder als normale buchung eingefügt werden
        Log.d(TAG, "deleted child booking at index " + childId);
        return database.delete(ExpensesDbHelper.TABLE_CHILD_BOOKINGS, ExpensesDbHelper.CHILD_BOOKINGS_COL_ID + " = ?", new String[]{"" + childId});
    }

    public int deleteChildrenFromParent(long parentId) {

        Log.d(TAG, "deleteChildrenFromParent: deleting children with parent id " + parentId);
        return database.delete(ExpensesDbHelper.TABLE_CHILD_BOOKINGS, ExpensesDbHelper.CHILD_BOOKINGS_COL_PARENT_BOOKING_ID + " = ?", new String[]{"" + parentId});
    }


    public long createCategory(Category category) {

        //TODO erstelle neue Kategorie wenn sie nicht bereits existiert
        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.CATEGORIES_COL_NAME, category.getCategoryName());
        values.put(ExpensesDbHelper.CATEGORIES_COL_COLOR, category.getColor());
        values.put(ExpensesDbHelper.CATEGORIES_COL_EXPENSE_TYPE, category.getDefaultExpenseType() ? 1 : 0);
        Log.d(TAG, "created new CATEGORY");

        return database.insert(ExpensesDbHelper.TABLE_CATEGORIES, null, values);
    }

    public ArrayList<Category> getAllCategories() {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CATEGORIES_COL_ID + ", "
                + ExpensesDbHelper.CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.CATEGORIES_COL_EXPENSE_TYPE
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
     * Convenience Method for getting a Category by its name
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
                + ExpensesDbHelper.CATEGORIES_COL_EXPENSE_TYPE
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
                + ExpensesDbHelper.CATEGORIES_COL_EXPENSE_TYPE
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
     * @param categoryId   Id of the Category which should be changed
     * @param categoryName new name of the CATEGORY
     * @return The id of the affected row
     */
    public int updateCategoryName(long categoryId, String categoryName) {

        throw new UnsupportedOperationException("Updating Categories us not Supported");//todo
    }

    /**
     * Convenience Method for deleting a Category by id
     *
     * @param categoryId Id of the Category which should be deleted
     * @return The result of the deleting operation
     */
    public int deleteCategory(long categoryId) {

        //TODO kategorien können nicht gelöscht werden, wenn es noch buchungen gibt, die in dieser Kategorie gemacht wurden
        Log.d(TAG, "delete Category + " + categoryId);
        return database.delete(ExpensesDbHelper.TABLE_CATEGORIES, ExpensesDbHelper.CATEGORIES_COL_ID + " = ?", new String[]{"" + categoryId});
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

    public int updateTemplate(long templateId, long bookingId) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID, bookingId);

        return database.update(ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS, values, ExpensesDbHelper.TEMPLATE_COL_ID + " = ?", new String[]{"" + templateId});
    }

    public int deleteTemplate(long templateId) {

        Log.d(TAG, "deleting template: " + templateId);
        return database.delete(ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS, ExpensesDbHelper.TEMPLATE_COL_ID + " = ?", new String[]{"" + templateId});
    }


    public long createRecurringBooking(long recurringBookingId, Calendar start, int frequency, Calendar end) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID, recurringBookingId);
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_START, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(start.getTime()));
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY, frequency);
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_END, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(end.getTime()));
        Log.d(TAG, "creating recurring booking: " + recurringBookingId);

        return database.insert(ExpensesDbHelper.TABLE_RECURRING_BOOKINGS, null, values);
    }

    public ArrayList<ExpenseObject> getRecurringBookings(Calendar dateRngStart, Calendar endDate) {//TODO nicht ganz zufrieden mit der funktion, bitte überdenken

        //exclude all events which end before the given date range
        String selectQuery = "SELECT "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID + ", "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY
                + " FROM " + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
                + " WHERE " + ExpensesDbHelper.RECURRING_BOOKINGS_COL_END + " > '"
                + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(dateRngStart.getTime()) + "';";
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
            String[] tmp = c.getString(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_START)).split("-");
            start.set(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]), Integer.parseInt(tmp[2]));

            //get end date of recurring booking
            tmp = c.getString(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_END)).split("-");
            end.set(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]), Integer.parseInt(tmp[2]));

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

    public int updateRecurringBooking(ExpenseObject newRecurringBooking, String startDate, int frequency, String endDate, long recurringId) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID, newRecurringBooking.getIndex());
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_START, startDate);
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY, frequency);
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_END, endDate);
        Log.d(TAG, "updateRecurringBooking: " + recurringId);

        return database.update(ExpensesDbHelper.TABLE_RECURRING_BOOKINGS, values, ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + " = ?", new String[]{"" + recurringId});
    }

    public int deleteRecurringBooking(long index) {

        Log.d(TAG, "deleteRecurringBooking: " + index);
        return database.delete(ExpensesDbHelper.TABLE_RECURRING_BOOKINGS, ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + "= ?", new String[]{"" + index});
    }


    public long createCurrency(Currency currency) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.CURRENCIES_COL_NAME, currency.getCurrencyName());
        values.put(ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME, currency.getCurrencyShortName());
        values.put(ExpensesDbHelper.CURRENCIES_COL_SYMBOL, currency.getCurrencySymbol());

        return database.insert(ExpensesDbHelper.TABLE_CURRENCIES, null, values);
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

    public Long getCurrencyId(String curShortName) {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CURRENCIES_COL_ID
                + " FROM " + ExpensesDbHelper.TABLE_CURRENCIES
                + " WHERE " + ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME + " = '" + curShortName + "';";
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getCurrencyId: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        long currencyId = c.getLong(c.getColumnIndex(ExpensesDbHelper.CURRENCIES_COL_ID));
        c.close();

        return currencyId;
    }

    public long updateCurrency(long index) {

        throw new UnsupportedOperationException("Updating Currencies is not Supported");//todo
    }

    public int deleteCurrency(long index) {

        Log.d(TAG, "deleteCurrency at index: " + index);
        return database.delete(ExpensesDbHelper.TABLE_CURRENCIES, ExpensesDbHelper.CURRENCIES_COL_ID + " = ?", new String[]{"" + index});
    }


    /**
     * Method for creating a new exchange rate based on currency objects
     *
     * @param fromCur    name of first currency
     * @param toCur      name of second currency
     * @param rate       exchange rate from currency one to currency two
     * @param serverDate fetching date
     * @return state of operation
     */
    public long createExchangeRate(Currency fromCur, Currency toCur, double rate, String serverDate) {

        return createExchangeRate(fromCur.getIndex(), toCur.getIndex(), rate, serverDate);
    }

    /**
     * Method for creating an exchange rate based on currency names
     *
     * @param fromCur    from currency object
     * @param toCur      to currency object
     * @param rate       exchange rate from currency on to curency two
     * @param serverDate fetching date
     * @return state of operation
     */
    public long createExchangeRate(String fromCur, String toCur, double rate, String serverDate) {

        return createExchangeRate(getCurrencyId(fromCur), getCurrencyId(toCur), rate, serverDate);
    }

    /**
     * Method for creating an exchange rate between to currencies
     *
     * @param fromCurIndex id of first currency
     * @param toCurIndex   id of second currency
     * @param rate         exchange rate from currency on to currency two
     * @param serverDate   fetching date
     * @return state of operation
     */
    public long createExchangeRate(long fromCurIndex, long toCurIndex, double rate, String serverDate) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_FROM_CURRENCY_ID, fromCurIndex);
        values.put(ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_TO_CURRENCY_ID, toCurIndex);
        values.put(ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_EXCHANGE_RATE, rate);
        values.put(ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_SERVER_DATE, serverDate);

        return database.insert(ExpensesDbHelper.TABLE_CURRENCY_EXCHANGE_RATES, null, values);
    }

    /**
     * Returns the Exchange rate between two currencies with additional date information
     *
     * @param fromCurIndex Database index of first Currency
     * @param toCurIndex   Database index of second Currency
     * @param date         Optional parameter which specifies the date of the ExchangeRate
     * @return HashMap(ExchangeRate, Download date of fetched exchange rate)
     */
    @Nullable
    public HashMap<Double, String> getExtendedExchangeRate(long fromCurIndex, long toCurIndex, String date) {

        HashMap<Double, String> extendedExchangeRateInfo = new HashMap<>();

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_EXCHANGE_RATE + ", "
                + ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_SERVER_DATE
                + " FROM " + ExpensesDbHelper.TABLE_CURRENCY_EXCHANGE_RATES
                + " WHERE "
                + "(" + ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_FROM_CURRENCY_ID + " = " + fromCurIndex
                + " OR " + ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_FROM_CURRENCY_ID + " = " + toCurIndex + ")"
                + " AND "
                + "(" + ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_TO_CURRENCY_ID + " = " + toCurIndex
                + " OR " + ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_TO_CURRENCY_ID + " = " + fromCurIndex + ")";

        if (date != null)
            selectQuery += " AND " + ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_SERVER_DATE + " = '" + date + "'";

        selectQuery += " ORDER BY " + ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_TIMESTAMP + " DESC;";
        Log.d(TAG, "getExchangeRate: " + selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getExtendedExchangeRate: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        if (!c.isAfterLast()) {

            Double exchangeRate = c.getDouble(c.getColumnIndex(ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_EXCHANGE_RATE));
            String serverDate = c.getString(c.getColumnIndex(ExpensesDbHelper.CURRENCY_EXCHANGE_RATES_COL_SERVER_DATE));
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
     * @param date         optional parameter which specifies the date of the ExchangeRate
     * @return The ExchangeRate if available null if not
     */
    @Nullable
    public Double getExchangeRate(long fromCurIndex, long toCurIndex, String date) {

        HashMap<Double, String> extendedExchangeRate = getExtendedExchangeRate(fromCurIndex, toCurIndex, date);

        if (extendedExchangeRate != null) {

            Map.Entry<Double, String> entry = extendedExchangeRate.entrySet().iterator().next();
            return entry.getKey();
        } else {

            return null;
        }
    }

    /**
     * Method for getting an exchange rate from rate to base to toCurIndex
     *
     * @param fromCurIndex id of convert to currency
     * @param date         date of the exchange rate
     * @return rate to base if available else null
     */
    @Nullable
    public Double getRateToBase(long fromCurIndex, String date) {

        SharedPreferences preferences = mContext.getSharedPreferences("UserSettings", Context.MODE_PRIVATE);
        long baseCurIndex = preferences.getLong("mainCurrencyIndex", 0);

        HashMap<Double, String> extendedExchangeRate = getExtendedExchangeRate(fromCurIndex, baseCurIndex, date);

        if (extendedExchangeRate != null) {

            Map.Entry<Double, String> entry = extendedExchangeRate.entrySet().iterator().next();
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
        values.put(ExpensesDbHelper.CONVERT_EXPENSES_STACK_COL_LATEST_TRY, expense.getDBDateTime());

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
}