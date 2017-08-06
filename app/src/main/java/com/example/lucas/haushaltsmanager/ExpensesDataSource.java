package com.example.lucas.haushaltsmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

class ExpensesDataSource {

    private static final String LOG_TAG = ExpensesDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private ExpensesDbHelper dbHelper;

    public ExpensesDataSource(Context context) {

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
        //int idAmount = cursor.getColumnIndex(ExpensesDbHelper.EXPENSES.COL.AMOUNT);
        //int idCategory = cursor.getColumnIndex(ExpensesDbHelper.CATEGORIES.COL.CATEGORY);
        //int idExpenditure = cursor.getColumnIndex(ExpensesDbHelper.EXPENSES.COL.EXPENDITURE);
        //int idTitle = cursor.getColumnIndex(ExpensesDbHelper.EXPENSES.COL.TITLE);
        //int idTag = cursor.getColumnIndex(ExpensesDbHelper.TAGS.COL.TAG);
        //int idDate = cursor.getColumnIndex(ExpensesDbHelper.EXPENSES.COL.DATE);
        //int idNotice = cursor.getColumnIndex(ExpensesDbHelper.EXPENSES.COL.NOTICE);
        int idAccount = cursor.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_ACCOUNT);

        expense.setIndex(cursor.getInt(idIndex));
        //expense.setAmount(cursor.getInt(idAmount));
        //expense.setExpenditure(cursor.getInt(idExpenditure));
        //expense.setTitle(cursor.getString(idTitle));
        //expense.setTag(cursor.getString(idTag));
        //String expenseDate = cursor.getString(idDate);
        //int day = Integer.parseInt(expenseDate.substring(0,1));
        //int month = Integer.parseInt(expenseDate.substring(3,4));
        //int year = Integer.parseInt(expenseDate.substring(6,7));
        //cal.set(year, month, day);
        //expense.setDate(cursor.getString(cal));
        //expense.setNotice(cursor.getString(idNotice));
        expense.setAccount(cursor.getString(idAccount));

        return expense;
    }


    /**
     * Convenience Method for creating a new Account
     *
     * @param account_name name of the account which should be created
     * @return the id of the created tag. -1 if the insertion failed
     */
    public long createAccount(String account_name) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.ACCOUNTS_COL_ACCOUNT, account_name);

        return database.insert(ExpensesDbHelper.TABLE_ACCOUNTS, null, values);
    }

    /**
     * Convenience Method for getting an specific Account
     *
     * @param account_id Id of the account which should be selected
     * @return The tag at the specified index
     */
    public String getAccount(long account_id) {

        String account = "";
        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_ACCOUNTS + " WHERE " + ExpensesDbHelper.ACCOUNTS_COL_ID + " = " + account_id;
        Log.d(LOG_TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);

        if (c != null) {

            c.moveToFirst();
            account = c.getString(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_ID));
        }

        c.close();

        return account;
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
     * @param account_id Id of the Account which should be updated
     * @param new_account_name New name of the Account
     * @return The number of rows affected
     */
    public int updateAccount(long account_id, String new_account_name) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.ACCOUNTS_COL_ACCOUNT, new_account_name);

        return database.update(ExpensesDbHelper.TABLE_ACCOUNTS, values, ExpensesDbHelper.ACCOUNTS_COL_ID + " = ?", new String[] {account_id + ""});
    }

    /**
     * Convenience Method for deleting a Account
     *
     * @param account_id the id of the entry which should be deleted
     * @return the number of affected rows
     */
    //TODO cant delete Account if it's still assigned to an Booking
    public int deleteAccount(long account_id) {

        Log.d(LOG_TAG, "deleted account at index " + account_id);
        return database.delete(ExpensesDbHelper.TABLE_ACCOUNTS, ExpensesDbHelper.ACCOUNTS_COL_ID + " = ?", new String[] {"" + account_id});
    }


    /**
     * Convenience Method for creating a new Tag
     *
     * @param tag_name name of the tag which should be created
     * @return the id of the created tag. -1 if the insertion failed
     */
    public long createTag(String tag_name) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.TAGS_COL_TAG_NAME, tag_name);

        Log.d(LOG_TAG, "created " + tag_name + " at Tag table");
        return database.insert(ExpensesDbHelper.TABLE_TAGS, null, values);
    }

    /**
     * Convenience Method for getting an specific Tag
     *
     * @param tag_id Id of the tag which should be selected
     * @return The tag at the specified index
     */
    public String getTag(long tag_id) {

        String tag = "";
        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_TAGS + " WHERE " + ExpensesDbHelper.TAGS_COL_ID + " = " + tag_id;
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
     * @param tag_id Id of the Tag which should be updated
     * @param new_tag_name New name of the Tag
     * @return The number of rows affected
     */
    public int updateTag(long tag_id, String new_tag_name) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.TAGS_COL_TAG_NAME, new_tag_name);

        return database.update(ExpensesDbHelper.TABLE_TAGS, values, ExpensesDbHelper.TAGS_COL_ID + " = ?", new String[] {tag_id + ""});
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

    private long[] getAllTagsToBooking(long bookingId) {


    }

    private long[] getAllBookingsToTag(long tagId) {


    }

    private int removeTagFromBooking(long bookingId, long tagId) {

        return database.delete(ExpensesDbHelper.TABLE_BOOKINGS_TAGS, );
    }


    public long createBooking(ExpenseObject expense) {


    }

    public ExpenseObject getBooking(long bookingId) {

        ExpenseObject expense;
        Cursor c = database.rawQuery(   );

        expense = cursorToExpense(c);
        c.close();
        return expense;
    }

    public int deleteBooking(long bookingId) {


    }
}
