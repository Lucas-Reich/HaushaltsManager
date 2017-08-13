package com.example.lucas.haushaltsmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

class ExpensesDataSource {

    //TODO wenn ein cursor nichts findet in einer datenbank dann kann man nicht mit c != null abfragen ob er leer ist || die abfrage ob ein cursor leer ist muss neu gemacht werden

    private static final String LOG_TAG = ExpensesDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private ExpensesDbHelper dbHelper;

    ExpensesDataSource(Context context) {

        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new ExpensesDbHelper(context);
    }

    void open() {
        Log.d(LOG_TAG, "Asked for a reference to a DB.");
        database = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "Obtained a reference to a Db. Way to Db: " + database.getPath());
    }

    void close() {
        dbHelper.close();
        Log.d(LOG_TAG, "Closed Db with the help of dbHelper.");
    }

    /**
     * Convenience Method for mapping an Cursor to a Booking
     *
     * @param cursor Cursor object obtained by a SQLITE search query
     * @return An remapped ExpenseObject
     */
    private ExpenseObject cursorToExpense(Cursor cursor) {

        ExpenseObject expense = new ExpenseObject();
        Calendar cal = Calendar.getInstance();

        int idIndex = cursor.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_BOOKING_ID);
        int idAmount = cursor.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_PRICE);
        int idCategory = cursor.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_F_CATEGORY_ID);
        int idExpenditure = cursor.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE);
        int idTitle = cursor.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_TITLE);
        int idDate = cursor.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_DATE);
        int idNotice = cursor.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_NOTICE);
        int idAccount = cursor.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_F_ACCOUNT_ID);

        expense.setIndex(cursor.getLong(idIndex));

        expense.setPrice(cursor.getDouble(idAmount));

        expense.setExpenditure(cursor.getInt(idExpenditure));

        expense.setTitle(cursor.getString(idTitle));

        Category category1 = getCategoryById(cursor.getLong(idCategory));
        expense.setCategory(category1);

        List<String> allTags = Arrays.asList(getTagsToBookingByBookingId(expense.getIndex()));
        expense.setTags(allTags);

        String expenseDate = cursor.getString(idDate);
        int day = Integer.parseInt(expenseDate.substring(0, 1));
        int month = Integer.parseInt(expenseDate.substring(3, 4));
        int year = Integer.parseInt(expenseDate.substring(6, 7));
        cal.set(year, month, day);
        expense.setDate(cal);

        expense.setNotice(cursor.getString(idNotice));

        Account account = getAccountById(cursor.getLong(idAccount));
        expense.setAccount(account);

        return expense;
    }


    /**
     * Convenience Method for creating a new Account
     *
     * @param account Account object  which should be created
     * @return the id of the created tag. -1 if the insertion failed
     */
    long createAccount(Account account) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.ACCOUNTS_COL_ACCOUNT, account.getAccountName());
        values.put(ExpensesDbHelper.ACCOUNTS_COL_BALANCE, account.getBalance());

        Log.d(LOG_TAG, "created account " + account.getAccountName() + " with a balance of " + account.getBalance());

        return database.insert(ExpensesDbHelper.TABLE_ACCOUNTS, null, values);
    }

    /**
     * Convenience Method for getting an specific Account
     *
     * @param accountId Id of the account which should be selected
     * @return The tag at the specified index
     */
    Account getAccountById(long accountId) {

        Account account;
        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_ACCOUNTS + " WHERE " + ExpensesDbHelper.ACCOUNTS_COL_ID + " = " + accountId;
        Log.d(LOG_TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);

        if (c != null) {

            c.moveToFirst();
            String accountName = c.getString(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_ACCOUNT));
            int accountBalance = c.getInt(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_BALANCE));

            account = new Account(accountId, accountName, accountBalance);
            return account;
        }

        c.close();
        return account = new Account();

    }

    Account getAccountByName(String accountName) {

        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_ACCOUNTS + " WHERE " + ExpensesDbHelper.ACCOUNTS_COL_ACCOUNT + " = \"" + accountName + "\"";
        Log.d(LOG_TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);

        if (c != null) {

            c.moveToFirst();
            long index = c.getLong(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_ID));
            String name = c.getString(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_ACCOUNT));
            int balance = c.getInt(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_BALANCE));

            return new Account(index, name, balance);
        }

        c.close();
        return new Account();

    }

    /**
     * Convenience Method for getting all available Accounts
     *
     * @return An array of strings with all Accounts inside TABLE_ACCOUNTS
     */
    Account[] getAllAccounts() {

        Account[] accounts;
        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_ACCOUNTS;
        Log.d(LOG_TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);

        c.moveToFirst();

        accounts = new Account[c.getCount()];

        for (int i = 0; i < c.getCount(); i++) {

            long index = c.getLong(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_ID));
            String accountName = c.getString(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_ACCOUNT));
            int balance = c.getInt(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_BALANCE));

            accounts[i] = new Account(index, accountName, balance);
            c.moveToNext();
        }

        c.close();

        return accounts;
    }

    String[] getAccountNames() {

        String[] accounts;
        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_ACCOUNTS;
        Log.d(LOG_TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        c.moveToFirst();

        accounts = new String[c.getCount()];
        int counter = 0;
        while (!c.isAfterLast()) {

            accounts[counter] = c.getString(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_ACCOUNT));
            counter++;
            c.moveToNext();
        }

        return accounts;
    }

    /**
     * Convenience Method for updating a Account
     *
     * @param account Account object with new balance or name
     * @return The number of rows affected
     */
    int updateAccount(Account account) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.ACCOUNTS_COL_ACCOUNT, account.getAccountName());

        String oldAccount = getAccountById(account.getIndex()).getAccountName();
        Log.d(LOG_TAG, "updated account " + oldAccount + " to " + account.getAccountName());

        return database.update(ExpensesDbHelper.TABLE_ACCOUNTS, values, ExpensesDbHelper.ACCOUNTS_COL_ID + " = ?", new String[]{account.getIndex() + ""});
    }

    /**
     * Convenience Method for deleting a Account
     *
     * @param account Account object which should be deleted
     * @return the number of affected rows
     */
    //TODO cant delete Account if it's still assigned to an Booking
    int deleteAccount(Account account) {

        Log.d(LOG_TAG, "deleted account " + account.getAccountName() + " at index " + account.getIndex());
        return database.delete(ExpensesDbHelper.TABLE_ACCOUNTS, ExpensesDbHelper.ACCOUNTS_COL_ID + " = ?", new String[]{"" + account.getIndex()});
    }


    /**
     * Convenience Method for creating a new Tag
     *
     * @param tagName name of the tag which should be created
     * @return the id of the created tag. -1 if the insertion failed
     */
    long createTag(String tagName) {

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
    String getTagById(long tagId) {

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
    String[] getAllTags() {

        String[] allTags;
        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_TAGS;
        Log.d(LOG_TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);

        c.moveToFirst();

        allTags = new String[c.getCount()];

        for (int i = 0; i < c.getCount(); i++) {

            allTags[i] = c.getString(c.getColumnIndex(ExpensesDbHelper.TAGS_COL_ID));
            c.moveToNext();
        }

        c.close();

        return allTags;
    }

    /**
     * Convenience Method for updating a Tag
     *
     * @param tagId      Id of the Tag which should be updated
     * @param newTagName New name of the Tag
     * @return The number of rows affected
     */
    int updateTag(long tagId, String newTagName) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.TAGS_COL_TAG_NAME, newTagName);

        return database.update(ExpensesDbHelper.TABLE_TAGS, values, ExpensesDbHelper.TAGS_COL_ID + " = ?", new String[]{tagId + ""});
    }

    /**
     * Convenience Method for deleting a Tag
     *
     * @param tag_id the id of the entry which should be deleted
     * @return the number of affected rows
     */
    //TODO cant delete Tag if it's still assigned to an Booking
    int deleteTag(long tag_id) {

        Log.d(LOG_TAG, "deleted tag at index " + tag_id);
        return database.delete(ExpensesDbHelper.TABLE_TAGS, ExpensesDbHelper.TAGS_COL_ID + " = ?", new String[]{"" + tag_id});
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
        values.put(ExpensesDbHelper.BOOKINGS_TAGS_COL_F_BOOKING_ID, tagId);
        values.put(ExpensesDbHelper.BOOKINGS_TAGS_COL_F_BOOKING_ID, bookingId);

        Log.d(LOG_TAG, "assigned tag " + tagId + " to booking " + bookingId);
        return database.insert(ExpensesDbHelper.TABLE_BOOKINGS_TAGS, null, values);
    }

    private long assignTagToBooking(long bookingId, String tagName) {
//TODO if booking has already tags (e.g. you are editing an existing booking) assigned to it don't create a duplicate
        long tagId = getTagByName(tagName);
        long index;

        if (tagId != -1) { // tag does not exist in table create it

            tagId = createTag(tagName);

        }

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.BOOKINGS_TAGS_COL_F_BOOKING_ID, bookingId);
        values.put(ExpensesDbHelper.BOOKINGS_TAGS_COL_F_TAG_ID, tagId);
        index =  database.insert(ExpensesDbHelper.TABLE_BOOKINGS_TAGS, null, values);

        Log.d(LOG_TAG, "assigned tag with id " + index + " to booking " + bookingId);
        return index;
    }

    /**
     * Class internal Method for requesting all Tags to a Booking by the BookingId
     *
     * @param bookingId The id of the Booking where the Tags should be outputted
     * @return All Tags to the specified booking in an String[]
     */
    private String[] getTagsToBookingByBookingId(long bookingId) {

        String[] tags;
        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_BOOKINGS_TAGS + " WHERE " + ExpensesDbHelper.BOOKINGS_TAGS_COL_F_BOOKING_ID + " = " + bookingId;
        Log.d(LOG_TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        c.moveToFirst();

        Log.d(LOG_TAG, DatabaseUtils.dumpCursorToString(c));

        if (!c.isAfterLast()) {

            int counter = 0;
            tags = new String[c.getCount()];

            while (!c.isAfterLast()) {

                tags[counter] = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_TAGS_COL_F_BOOKING_ID));
                counter++;
            }
        } else {

            tags = new String[]{""};
        }

        c.close();
        return tags;
    }

    /**
     * Class internal Method for requesting all Bookings to a specified Tag
     *
     * @param tagId Id of the Tag where all Bookings are requested
     * @return All ids of the affected Bookings
     */
    private long[] getBookingsToTagByTagId(long tagId) {

        long[] bookings;
        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_BOOKINGS_TAGS + " WHERE " + ExpensesDbHelper.BOOKINGS_TAGS_COL_F_TAG_ID + " = " + tagId;
        Log.d(LOG_TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);

        if (c != null) {

            c.moveToFirst();
            int counter = 0;
            bookings = new long[c.getCount()];

            while (!c.isAfterLast()) {

                bookings[counter] = c.getLong(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_TAGS_COL_F_TAG_ID));
                counter++;
            }
        } else {

            bookings = new long[]{};
        }

        c.close();
        return bookings;
    }

    /**
     * Class internal Method for removing an Tag from a Booking
     *
     * @param bookingTagId Id of the row which has to be deleted
     * @return The result of the operation
     */
    private int removeTagFromBooking(long bookingTagId) {

        Log.d(LOG_TAG, "deleted tag from booking at index " + bookingTagId);
        return database.delete(ExpensesDbHelper.TABLE_BOOKINGS_TAGS, ExpensesDbHelper.BOOKINGS_TAGS_COL_ID + " = ?", new String[]{"" + bookingTagId});
    }

    private long getTagByName(String tagName) {

        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_TAGS + " WHERE " + ExpensesDbHelper.TAGS_COL_TAG_NAME + " = \"" + tagName + "\"";
        Log.d(LOG_TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);

        c.moveToFirst();

        if (c.isAfterLast()) {

            return -1;
        } else {

            return c.getLong(c.getColumnIndex(ExpensesDbHelper.TAGS_COL_ID));
        }
    }


    /**
     * Convenience Method for creating a Booking
     *
     * @param expense The expense which has to be stored in the DB
     * @return Id of the created Booking
     */
    long createBooking(ExpenseObject expense) {

        ContentValues values = new ContentValues();
        values.put("price", expense.getPrice());

        //TODO if Category does not exist already create it
        long categoryId = expense.getCategory().getIndex();
        values.put("f_category_id", categoryId);
        values.put("expenditure", expense.getExpenditure());
        values.put("title", expense.getTitle());
        values.put("date", expense.getDate());
        values.put("notice", expense.getNotice());

        //TODO if Account does not exist already create it
        long accountId = expense.getAccount().getIndex();
        values.put("f_account_id", accountId);

        //assign all chosen tags to the booking
        for (String tag : expense.getTags()) {

            assignTagToBooking(expense.getIndex(), tag);
        }

        Log.d(LOG_TAG, "created expense at Booking table");
        return database.insert(ExpensesDbHelper.TABLE_BOOKINGS, null, values);
    }

    /**
     * Convenience Method for getting a Booking
     *
     * @param bookingId Get the Booking values to the selected id
     * @return The requested expense
     */
    ExpenseObject getBookingById(long bookingId) {

        ExpenseObject expense;
        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_BOOKINGS + " WHERE " + ExpensesDbHelper.BOOKINGS_COL_BOOKING_ID + " = " + bookingId;
        Log.d(LOG_TAG, selectQuery);
        Cursor c = database.rawQuery(selectQuery, null);

        c.moveToFirst();

        Log.d(LOG_TAG, DatabaseUtils.dumpCursorToString(c));
        expense = cursorToExpense(c);
        c.close();
        return expense;
    }

    /**
     * Convenience Method for updating a Booking
     *
     * @param bookingId  Id of the Booking which should be changed
     * @param newExpense Expense with the new values
     * @return the number of affected rows
     */
    int updateBooking(long bookingId, ExpenseObject newExpense) {

        ContentValues values = new ContentValues();
        values.put("price", newExpense.getPrice());

        //TODO if Category does not exist already create it
        long categoryId = newExpense.getCategory().getIndex();
        values.put("f_category_id", categoryId);
        values.put("expenditure", newExpense.getExpenditure());
        values.put("title", newExpense.getTitle());
        values.put("date", newExpense.getDate());
        values.put("notice", newExpense.getNotice());

        //TODO if Account does not exist already create it
        long accountId = newExpense.getAccount().getIndex();
        values.put("f_account_id", accountId);

        Log.d(LOG_TAG, "changed booking " + bookingId);
        return database.update(ExpensesDbHelper.TABLE_BOOKINGS, values, ExpensesDbHelper.BOOKINGS_COL_BOOKING_ID + " = ?", new String[]{"" + bookingId});
    }

    /**
     * Convenience Method for deleting a Booking
     *
     * @param bookingId Id of the Booking which should be deleted
     * @return the number of affected rows
     */
    int deleteBooking(long bookingId) {

        Log.d(LOG_TAG, "deleted booking at index " + bookingId);
        return database.delete(ExpensesDbHelper.TABLE_BOOKINGS, ExpensesDbHelper.BOOKINGS_COL_BOOKING_ID + " = ?", new String[]{"" + bookingId});
    }


    long createCategory(String categoryName, int color) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.CATEGORIES_COL_CATEGORY_NAME, categoryName);
        values.put(ExpensesDbHelper.CATEGORIES_COL_COLOR, color);
        Log.d(LOG_TAG, "created new category " + categoryName);

        return database.insert(ExpensesDbHelper.TABLE_CATEGORIES, null, values);
    }

    /**
     * Convenience Method for getting a Category by its name
     *
     * @param category Name of the category
     * @return Returns an Category object
     */
    Category getCategoryByName(String category) {

        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_CATEGORIES + " WHERE " + ExpensesDbHelper.CATEGORIES_COL_CATEGORY_NAME + " = \"" + category + "\"";
        Log.d(LOG_TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Category category1 = new Category();

        if (c != null) {

            c.moveToFirst();
            category1.setIndex(c.getLong(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_ID)));
            category1.setCategoryName(c.getString(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_CATEGORY_NAME)));
            category1.setColor(c.getInt(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_COLOR)));
        }

        c.close();

        return category1;
    }

    /**
     * Convenience Method for getting a Category
     *
     * @param categoryId index of the desired Category
     * @return Returns an Category object
     */
    Category getCategoryById(long categoryId) {

        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_CATEGORIES + " WHERE " + ExpensesDbHelper.CATEGORIES_COL_ID + " = " + categoryId;
        Log.d(LOG_TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);

        Log.d(LOG_TAG, DatabaseUtils.dumpCursorToString(c));

        Category category = new Category();

        if (c != null) {

            c.moveToFirst();
            category.setIndex(c.getLong(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_ID)));
            category.setCategoryName(c.getString(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_CATEGORY_NAME)));
            category.setColor(c.getInt(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_COLOR)));
        }

        c.close();

        return category;
    }

    /**
     * Convenience Method for updating a Categories color
     *
     * @param color      Int value of the new color
     * @param categoryId Id of the Category which should be changed
     * @return The id of the affected row
     */
    int updateCategory(int color, long categoryId) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.CATEGORIES_COL_COLOR, color);
        values.put(ExpensesDbHelper.CATEGORIES_COL_CATEGORY_NAME, getCategoryById(categoryId).getCategoryName());

        Log.d(LOG_TAG, "update category " + categoryId);

        return database.update(ExpensesDbHelper.TABLE_CATEGORIES, values, ExpensesDbHelper.CATEGORIES_COL_ID + " = ?", new String[]{"" + categoryId});
    }

    /**
     * Convenience Method for updating a Categories color
     *
     * @param categoryName new name of the category
     * @param categoryId   Id of the Category which should be changed
     * @return The id of the affected row
     */
    int updateCategory(String categoryName, long categoryId) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.CATEGORIES_COL_COLOR, getCategoryById(categoryId).getColor());
        values.put(ExpensesDbHelper.CATEGORIES_COL_CATEGORY_NAME, categoryName);

        Log.d(LOG_TAG, "update category " + categoryId);

        return database.update(ExpensesDbHelper.TABLE_CATEGORIES, values, ExpensesDbHelper.CATEGORIES_COL_ID + " = ?", new String[]{"" + categoryId});
    }

    /**
     * Convenience Method for deleting a Category by id
     *
     * @param categoryId Id of the Category which should be deleted
     * @return The result of the deleting operation
     */
    int deleteCategory(long categoryId) {

        Log.d(LOG_TAG, "delete Category + " + categoryId);
        return database.delete(ExpensesDbHelper.TABLE_CATEGORIES, ExpensesDbHelper.CATEGORIES_COL_ID + " = ?", new String[]{"" + categoryId});
    }
}
