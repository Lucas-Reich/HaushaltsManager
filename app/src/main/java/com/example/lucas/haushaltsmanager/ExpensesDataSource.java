package com.example.lucas.haushaltsmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

class ExpensesDataSource {

    private static final String LOG_TAG = ExpensesDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private ExpensesDbHelper dbHelper;

    ExpensesDataSource(Context context) {

        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new ExpensesDbHelper(context);
    }

    public void open() {
        Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "Datenbank-Referenz erhalten. Pfad zur Datenbank: " + database.getPath());
    }

    public void close() {
        dbHelper.close();
        Log.d(LOG_TAG, "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }

    private ExpenseObject cursorToExpense(Cursor cursor) {

        ExpenseObject expense = new ExpenseObject();
        Calendar cal = Calendar.getInstance();

        int idIndex = cursor.getColumnIndex(ExpensesDbHelper.TAGS_COL_ID);
        int idAmount = cursor.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_PRICE);
        int idCategory = cursor.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_F_CATEGORY_ID);
        int idExpenditure = cursor.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE);
        int idTitle = cursor.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_TITLE);
        int idDate = cursor.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_DATE);
        int idNotice = cursor.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_NOTICE);
        int idAccount = cursor.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_ACCOUNT);

        expense.setIndex(cursor.getInt(idIndex));

        expense.setPrice(cursor.getDouble(idAmount));

        expense.setExpenditure(cursor.getInt(idExpenditure));

        expense.setTitle(cursor.getString(idTitle));

        String category = getCategoryById(cursor.getLong(idCategory));
        expense.setCategory(category);

        List<String> allTags = Arrays.asList(getAllTagsToBooking(idIndex));
        expense.setTags(allTags);

        String expenseDate = cursor.getString(idDate);
        int day = Integer.parseInt(expenseDate.substring(0,1));
        int month = Integer.parseInt(expenseDate.substring(3,4));
        int year = Integer.parseInt(expenseDate.substring(6,7));
        cal.set(year, month, day);
        expense.setDate(cal);

        expense.setNotice(cursor.getString(idNotice));

        String account = getTagById(cursor.getLong(idAccount));
        expense.setAccount(account);

        return expense;
    }


    /**
     * Convenience Method for creating a new Account
     *
     * @param accountName name of the account which should be created
     * @return the id of the created tag. -1 if the insertion failed
     */
    public long createAccount(String accountName) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.ACCOUNTS_COL_ACCOUNT, accountName);

        Log.d(LOG_TAG, "created " + accountName + " at Accounts table");
        return database.insert(ExpensesDbHelper.TABLE_ACCOUNTS, null, values);
    }

    /**
     * Convenience Method for getting an specific Account
     *
     * @param accountId Id of the account which should be selected
     * @return The tag at the specified index
     */
    public String getAccountByName(long accountId) {

        String account = "";
        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_ACCOUNTS + " WHERE " + ExpensesDbHelper.ACCOUNTS_COL_ID + " = " + accountId;
        Log.d(LOG_TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);

        if (c != null) {

            c.moveToFirst();
            account = c.getString(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_ID));
        }

        c.close();

        return account;
    }

    public long getAccountByName(String accountName) {

        long accountId = 0;
        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_ACCOUNTS + " WHERE " + ExpensesDbHelper.ACCOUNTS_COL_ACCOUNT + " = " + accountId;
        Log.d(LOG_TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);

        if (c != null) {

            c.moveToFirst();
            accountId = c.getLong(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_ACCOUNT));
        }

        c.close();

        return accountId;
    }

    /**
     * Convenience Method for getting all available Accounts
     *
     * @return An array of strings with all Accounts inside TABLE_ACCOUNTS
     */
    public String[] getAllAccounts() {

        String[] allAccounts;
        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_TAGS;
        Log.d(LOG_TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);

        c.moveToFirst();

        allAccounts = new String[c.getCount()];

        for(int i = 0; i < c.getCount(); i++) {

            allAccounts[i] = c.getString(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_ID));
            c.moveToNext();
        }

        c.close();

        return allAccounts;
    }

    /**
     * Convenience Method for updating a Account
     *
     * @param accountId Id of the Account which should be updated
     * @param newAccountName New name of the Account
     * @return The number of rows affected
     */
    public int updateAccount(long accountId, String newAccountName) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.ACCOUNTS_COL_ACCOUNT, newAccountName);

        return database.update(ExpensesDbHelper.TABLE_ACCOUNTS, values, ExpensesDbHelper.ACCOUNTS_COL_ID + " = ?", new String[] {accountId + ""});
    }

    /**
     * Convenience Method for deleting a Account
     *
     * @param accountId the id of the entry which should be deleted
     * @return the number of affected rows
     */
    //TODO cant delete Account if it's still assigned to an Booking
    public int deleteAccount(long accountId) {

        Log.d(LOG_TAG, "deleted account at index " + accountId);
        return database.delete(ExpensesDbHelper.TABLE_ACCOUNTS, ExpensesDbHelper.ACCOUNTS_COL_ID + " = ?", new String[] {"" + accountId});
    }


    /**
     * Convenience Method for creating a new Tag
     *
     * @param tagName name of the tag which should be created
     * @return the id of the created tag. -1 if the insertion failed
     */
    public long createTag(String tagName) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.TAGS_COL_TAG_NAME, tagName);

        Log.d(LOG_TAG, "created " + tagName + " at Tag table");
        return database.insert(ExpensesDbHelper.TABLE_TAGS, null, values);
    }

    /**
     * Convenience Method for getting an specific Tag
     *
     * @param tagId Id of the tag which should be selected
     * @return The tag at the specified index
     */
    public String getTagById(long tagId) {

        String tag = "";
        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_TAGS + " WHERE " + ExpensesDbHelper.TAGS_COL_ID + " = " + tagId;
        Log.d(LOG_TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);

        if (c != null) {

            c.moveToFirst();
            tag = c.getString(c.getColumnIndex(ExpensesDbHelper.TAGS_COL_ID));
        }

        c.close();

        return tag;
    }

    /**
     * Convenience Method for getting all available Tags
     *
     * @return An array of strings with all Tags inside TABLE_TAGS
     */
    public String[] getAllTags() {

        String[] allTags;
        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_TAGS;
        Log.d(LOG_TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);

        c.moveToFirst();

        allTags = new String[c.getCount()];

        for(int i = 0; i < c.getCount(); i++) {

            allTags[i] = c.getString(c.getColumnIndex(ExpensesDbHelper.TAGS_COL_ID));
            c.moveToNext();
        }

        c.close();

        return allTags;
    }

    /**
     * Convenience Method for updating a Tag
     *
     * @param tagId Id of the Tag which should be updated
     * @param newTagName New name of the Tag
     * @return The number of rows affected
     */
    public int updateTag(long tagId, String newTagName) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.TAGS_COL_TAG_NAME, newTagName);

        return database.update(ExpensesDbHelper.TABLE_TAGS, values, ExpensesDbHelper.TAGS_COL_ID + " = ?", new String[] {tagId + ""});
    }
    /**
     * Convenience Method for deleting a Tag
     *
     * @param tag_id the id of the entry which should be deleted
     * @return the number of affected rows
     */
    //TODO cant delete Tag if it's still assigned to an Booking
    public int deleteTag(long tag_id) {

        Log.d(LOG_TAG, "deleted tag at index " + tag_id);
        return database.delete(ExpensesDbHelper.TABLE_TAGS, ExpensesDbHelper.TAGS_COL_ID + " = ?", new String[] {"" + tag_id});
    }


    private long assignTagToBooking(long bookingId, long tagId) {

        return
    }

    private String[] getAllTagsToBooking(long bookingId) {


    }

    private long[] getAllBookingsToTag(long tagId) {


    }

    private int removeTagFromBooking(long bookingId, long tagId) {

        return database.delete(ExpensesDbHelper.TABLE_BOOKINGS_TAGS, );
    }


    /**
     * Convenience Method for creating a Booking
     *
     * @param expense The expense which has to be stored in the DB
     * @return Id of the created Booking
     */
    public long createBooking(ExpenseObject expense) {

        ContentValues values = new ContentValues();
        values.put("price", expense.getPrice());

        //TODO if Category does not exist already create it
        long categoryId = getCategoryByName(expense.getCategory());
        values.put("f_category_id", categoryId);
        values.put("expenditure", expense.getExpenditure());
        values.put("title", expense.getTitle());
        values.put("date", expense.getDate());
        values.put("notice", expense.getNotice());

        //TODO if Account does not exist already create it
        long accountId = getAccountByName(expense.getAccount());
        values.put("f_account_id", accountId);

        Log.d(LOG_TAG, "created expense at Booking table");
        return database.insert(ExpensesDbHelper.TABLE_BOOKINGS, null, values);
    }

    /**
     * Convenience Method for getting a Booking
     *
     * @param bookingId Get the Booking values to the selected id
     * @return The requested expense
     */
    public ExpenseObject getBookingById(long bookingId) {

        ExpenseObject expense;
        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_BOOKINGS + " WHERE " + ExpensesDbHelper.BOOKINGS_COL_BOOKING_ID + " = " + bookingId;
        Log.d(LOG_TAG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);

        expense = cursorToExpense(c);
        c.close();
        return expense;
    }

    /**
     * Convenience Method for updating a Booking
     *
     * @param bookingId Id of the Booking which should be changed
     * @param newExpense Expense with the new values
     * @return the number of affected rows
     */
    public int updateBooking(long bookingId, ExpenseObject newExpense) {

        ContentValues values = new ContentValues();
        values.put("price", newExpense.getPrice());

        //TODO if Category does not exist already create it
        long categoryId = getCategoryByName(newExpense.getCategory());
        values.put("f_category_id", categoryId);
        values.put("expenditure", newExpense.getExpenditure());
        values.put("title", newExpense.getTitle());
        values.put("date", newExpense.getDate());
        values.put("notice", newExpense.getNotice());

        //TODO if Account does not exist already create it
        long accountId = getAccountByName(newExpense.getAccount());
        values.put("f_account_id", accountId);

        Log.d(LOG_TAG, "changed booking " + bookingId);
        return database.update(ExpensesDbHelper.TABLE_BOOKINGS, values, ExpensesDbHelper.BOOKINGS_COL_BOOKING_ID + " = ?", new String[] {"" + bookingId});
    }

    /**
     * Convenience Method for deleting a Booking
     *
     * @param bookingId Id of the Booking which should be deleted
     * @return the number of affected rows
     */
    public int deleteBooking(long bookingId) {

        Log.d(LOG_TAG, "deleted booking at index " + bookingId);
        return database.delete(ExpensesDbHelper.TABLE_BOOKINGS, ExpensesDbHelper.BOOKINGS_COL_BOOKING_ID + " = ?", new String[] {"" + bookingId});
    }


    private long getCategoryByName(String category) {

        long categoryId = 0;
        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_CATEGORIES + " WHERE " + ExpensesDbHelper.CATEGORIES_COL_CATEGORY_NAME + " = " + category;
        Log.d(LOG_TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);

        if (c != null) {

            c.moveToFirst();
            categoryId = c.getLong(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_CATEGORY_NAME));
        }

        c.close();

        return categoryId;
    }

    public String getCategoryById(long categoryId) {

        String categoryId = "";
        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_CATEGORIES + " WHERE " + ExpensesDbHelper.CATEGORIES_COL_ID + " = " + categoryId;
        Log.d(LOG_TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);

        if (c != null) {

            c.moveToFirst();
            categoryId = c.getString(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_ID));
        }

        c.close();

        return categoryId;
    }
}
