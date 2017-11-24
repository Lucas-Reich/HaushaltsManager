package com.example.lucas.haushaltsmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

class ExpensesDataSource {

    private static final String TAG = ExpensesDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private ExpensesDbHelper dbHelper;

    ExpensesDataSource(Context context) {

        Log.d(TAG, "Unsere DataSource erzeugt jetzt den dbHelper.");
        dbHelper = new ExpensesDbHelper(context);
    }

    void open() {
        Log.d(TAG, "Asked for a reference to a DB.");
        database = dbHelper.getWritableDatabase();
        Log.d(TAG, "Obtained a reference to a Db. Way to Db: " + database.getPath());
    }

    void close() {
        dbHelper.close();
        Log.d(TAG, "Closed Db with the help of dbHelper.");
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

        int idIndex = cursor.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_ID);
        int idAmount = cursor.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_PRICE);
        int idCategory = cursor.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID);
        int idExpenditure = cursor.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE);
        int idTitle = cursor.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_TITLE);
        int idDate = cursor.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_DATE);
        int idNotice = cursor.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_NOTICE);
        int idAccount = cursor.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID);

        expense.setIndex(cursor.getLong(idIndex));

        expense.setPrice(cursor.getDouble(idAmount));

        expense.setExpenditure(cursor.getInt(idExpenditure));

        expense.setTitle(cursor.getString(idTitle));

        Category category1 = getCategoryById(cursor.getLong(idCategory));
        expense.setCategory(category1);

        List<String> allTags = Arrays.asList(getTagsToBookingByBookingId(expense.getIndex()));
        expense.setTags(allTags);

        String[] fullDate = cursor.getString(idDate).split(" ");
        String[] expenseDate = fullDate[0].split("-");
        String[] expenseTime = fullDate[1].split(":");

        int year = Integer.parseInt(expenseDate[0]);
        int month = Integer.parseInt(expenseDate[1]);
        int day = Integer.parseInt(expenseDate[2]);
        int hour = Integer.parseInt(expenseTime[0]);
        int minute = Integer.parseInt(expenseTime[1]);
        int second = Integer.parseInt(expenseTime[2]);

        cal.set(year, month, day, hour, minute, second);
        expense.setDate(cal);

        expense.setNotice(cursor.getString(idNotice));

        //booking has children trigger
        if (cursor.getLong(idAccount) == 9999) {

            expense.addChildren(getChildsToParent(expense.getIndex()));
            expense.setAccount(new Account(9999, "", 0));
        } else {

            expense.setAccount(getAccountById(cursor.getLong(idAccount)));
        }

        return expense;
    }

    private long createDummyExpense() {

        ExpenseObject dummyExpense = new ExpenseObject();
        dummyExpense.setTitle("Dummy");
        dummyExpense.setPrice(0);
        dummyExpense.setExpenditure(true);
        dummyExpense.setCategory(new Category());
        dummyExpense.setAccount(new Account(9999, "", 0));
        dummyExpense.setDate(Calendar.getInstance());

        return createBooking(dummyExpense);
    }


    /**
     * Convenience Method for creating a new Account
     *
     * @param account Account object  which should be created
     * @return the id of the created tag. -1 if the insertion failed
     */
    long createAccount(Account account) {


        //TODO erstelle einen Account nur wenn es ihn noch nicht gibt
        //wenn es ihn gibt frage nach und überschreibe dann

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.ACCOUNTS_COL_NAME, account.getAccountName());
        values.put(ExpensesDbHelper.ACCOUNTS_COL_BALANCE, account.getBalance());

        Log.d(TAG, "created account " + account.getAccountName() + " with a balance of " + account.getBalance());

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
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, DatabaseUtils.dumpCursorToString(c));

        c.moveToFirst();
        if (!c.isAfterLast()) {

            String accountName = c.getString(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_NAME));
            int accountBalance = c.getInt(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_BALANCE));

            account = new Account(accountId, accountName, accountBalance);
            return account;
        }

        c.close();
        return new Account();
    }

    /**
     * @param accountName
     * @return
     */
    Account getAccountByName(String accountName) {

        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_ACCOUNTS + " WHERE " + ExpensesDbHelper.ACCOUNTS_COL_NAME + " = \"" + accountName + "\"";
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, DatabaseUtils.dumpCursorToString(c));

        c.moveToFirst();
        if (!c.isAfterLast()) {

            long index = c.getLong(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_ID));
            String name = c.getString(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_NAME));
            int balance = c.getInt(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_BALANCE));

            return new Account(index, name, balance);
        }

        c.close();
        return new Account();
    }

    ArrayList<Account> getAllAccounts() {

        ArrayList<Account> accounts = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_ACCOUNTS;
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, DatabaseUtils.dumpCursorToString(c));

        c.moveToFirst();
        while (!c.isAfterLast()) {

            long index = c.getLong(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_ID));
            String accountName = c.getString(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_NAME));
            int balance = c.getInt(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_BALANCE));

            accounts.add(new Account(index, accountName, balance));
            c.moveToNext();
        }

        return accounts;
    }

    /**
     * Convenience Method for getting all available Accounts
     *
     * @return An array of strings with all Accounts inside TABLE_ACCOUNTS
     */
    Account[] getAllAccountsOld() {

        Account[] accounts;
        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_ACCOUNTS;
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, DatabaseUtils.dumpCursorToString(c));

        c.moveToFirst();
        accounts = new Account[c.getCount()];

        for (int i = 0; i < c.getCount(); i++) {

            long index = c.getLong(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_ID));
            String accountName = c.getString(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_NAME));
            int balance = c.getInt(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_BALANCE));

            accounts[i] = new Account(index, accountName, balance);
            c.moveToNext();
        }

        c.close();

        return accounts;
    }

    /**
     * @return
     */
    String[] getAccountNames() {

        String[] accounts;
        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_ACCOUNTS;
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        accounts = new String[c.getCount()];
        int counter = 0;
        while (!c.isAfterLast()) {

            accounts[counter] = c.getString(c.getColumnIndex(ExpensesDbHelper.ACCOUNTS_COL_NAME));
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
        values.put(ExpensesDbHelper.ACCOUNTS_COL_NAME, account.getAccountName());

        String oldAccount = getAccountById(account.getIndex()).getAccountName();
        Log.d(TAG, "updated account " + oldAccount + " to " + account.getAccountName());

        return database.update(ExpensesDbHelper.TABLE_ACCOUNTS, values, ExpensesDbHelper.ACCOUNTS_COL_ID + " = ?", new String[]{account.getIndex() + ""});
    }

    /**
     * Convenience Method for deleting a Account
     *
     * @param account Account object which should be deleted
     * @return the number of affected rows
     */
    int deleteAccount(Account account) {

        //TODO ein konto kann nicht gelöscht werden wenn noch eine buchung für das konto existiert
        Log.d(TAG, "deleted account " + account.getAccountName() + " at index " + account.getIndex());
        return database.delete(ExpensesDbHelper.TABLE_ACCOUNTS, ExpensesDbHelper.ACCOUNTS_COL_ID + " = ?", new String[]{"" + account.getIndex()});
    }


    /**
     * Convenience Method for creating a new Tag
     *
     * @param tagName name of the tag which should be created
     * @return the id of the created tag. -1 if the insertion failed
     */
    long createTag(String tagName) {


        //TODO create tag wenn es noch nicht existiert
        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.TAGS_COL_NAME, tagName);

        Log.d(TAG, "created " + tagName + " at Tag table");
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
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, DatabaseUtils.dumpCursorToString(c));

        c.moveToFirst();
        if (!c.isAfterLast()) {

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
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, DatabaseUtils.dumpCursorToString(c));

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
        values.put(ExpensesDbHelper.TAGS_COL_NAME, newTagName);

        return database.update(ExpensesDbHelper.TABLE_TAGS, values, ExpensesDbHelper.TAGS_COL_ID + " = ?", new String[]{tagId + ""});
    }

    /**
     * Convenience Method for deleting a Tag
     *
     * @param tag_id the id of the entry which should be deleted
     * @return the number of affected rows
     */
    int deleteTag(long tag_id) {

        //TODO ein tag kann nicht gelöscht werden, wenn es noch einer buchung zugeordnet ist
        Log.d(TAG, "deleted tag at index " + tag_id);
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
        values.put(ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_ID, tagId);
        values.put(ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_ID, bookingId);

        Log.d(TAG, "assigned tag " + tagId + " to booking " + bookingId);
        return database.insert(ExpensesDbHelper.TABLE_BOOKINGS_TAGS, null, values);
    }

    /**
     * @param bookingId
     * @param tagName
     * @return
     */
    private long assignTagToBooking(long bookingId, String tagName) {
//TODO if booking has already tags (e.g. you are editing an existing booking) assigned to it don't create a duplicate
        long tagId = getTagByName(tagName);
        long index;

        if (tagId != -1) { // tag does not exist in table create it

            tagId = createTag(tagName);

        }

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_ID, bookingId);
        values.put(ExpensesDbHelper.BOOKINGS_TAGS_COL_TAG_ID, tagId);
        index = database.insert(ExpensesDbHelper.TABLE_BOOKINGS_TAGS, null, values);

        Log.d(TAG, "assigned tag with id " + index + " to booking " + bookingId);
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
        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_BOOKINGS_TAGS + " WHERE " + ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_ID + " = " + bookingId;
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        c.moveToFirst();

        Log.d(TAG, DatabaseUtils.dumpCursorToString(c));

        if (!c.isAfterLast()) {

            int counter = 0;
            tags = new String[c.getCount()];

            while (!c.isAfterLast()) {

                tags[counter] = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_ID));
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
        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_BOOKINGS_TAGS + " WHERE " + ExpensesDbHelper.BOOKINGS_TAGS_COL_TAG_ID + " = " + tagId;
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, DatabaseUtils.dumpCursorToString(c));

        c.moveToFirst();
        if (!c.isAfterLast()) {

            int counter = 0;
            bookings = new long[c.getCount()];

            while (!c.isAfterLast()) {

                bookings[counter] = c.getLong(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_TAGS_COL_TAG_ID));
                counter++;
            }
        } else {

            bookings = new long[]{ };
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

        Log.d(TAG, "deleted tag from booking at index " + bookingTagId);
        return database.delete(ExpensesDbHelper.TABLE_BOOKINGS_TAGS, ExpensesDbHelper.BOOKINGS_TAGS_COL_ID + " = ?", new String[]{"" + bookingTagId});
    }

    /**
     * @param tagName name of the tag
     * @return db id of the tag
     */
    private long getTagByName(String tagName) {

        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_TAGS + " WHERE " + ExpensesDbHelper.TAGS_COL_NAME + " = \"" + tagName + "\"";
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, DatabaseUtils.dumpCursorToString(c));

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
        values.put(ExpensesDbHelper.BOOKINGS_COL_PRICE, expense.getUnsignedPrice());
        values.put(ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID, expense.getCategory().getIndex());
        values.put(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE, expense.getExpenditure());
        values.put(ExpensesDbHelper.BOOKINGS_COL_TITLE, expense.getTitle());
        values.put(ExpensesDbHelper.BOOKINGS_COL_DATE, expense.getDBDate());
        values.put(ExpensesDbHelper.BOOKINGS_COL_NOTICE, expense.getNotice());
        values.put(ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID, expense.getAccount().getIndex());

        //assign all chosen tags to the booking
        for (String tag : expense.getTags()) {

            //TODO enable tag behaviour
            //habe es verboten da aus irgendeinem grund zu jeder buchung 2 booking_tags angelegt wurden ohne das die buchung tag hatte
        }

        Log.d(TAG, "created expense at Booking table");
        return database.insert(ExpensesDbHelper.TABLE_BOOKINGS, null, values);
    }

    void createBookings(List<ExpenseObject> bookings) {

        for (ExpenseObject booking : bookings) {

            createBooking(booking);
        }
    }

    /**
     * Convenience Method for getting a Booking
     *
     * @param bookingId Get the Booking values to the selected id
     * @return The requested expense
     */
    ExpenseObject getBookingById(long bookingId) {

        String selectQuery;
        selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_BOOKINGS;
        selectQuery += " WHERE " + ExpensesDbHelper.BOOKINGS_COL_ID;
        selectQuery += " = " + bookingId + ";";
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, DatabaseUtils.dumpCursorToString(c));

        c.moveToFirst();
        return cursorToExpense(c);
    }

    /**
     * Get all available bookings
     *
     * @return ExpenseObject ArrayList
     */
    ArrayList<ExpenseObject> getAllBookings() {

        return getAllBookings("", "");
    }

    /**
     * @param startDate
     * @param endDate
     * @return
     */
    ArrayList<ExpenseObject> getAllBookings(String startDate, String endDate) {

        ArrayList<ExpenseObject> bookings = new ArrayList<>();

        String selectQuery;

        if (startDate.length() != 0 && endDate.length() != 0) {

            selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_BOOKINGS;
            selectQuery += " WHERE " + ExpensesDbHelper.BOOKINGS_COL_DATE + " BETWEEN '" + startDate + "' AND '" + endDate + "'";
            selectQuery += " ORDER BY " + ExpensesDbHelper.BOOKINGS_COL_DATE + " ASC;";
        } else {

            selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_BOOKINGS;
        }
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "found: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        if (!c.isAfterLast()) {

            while (!c.isAfterLast()) {

                bookings.add(cursorToExpense(c));
                c.moveToNext();
            }
        }

        return bookings;
    }

    /**
     * Convenience Method for updating a Booking
     *
     * @param bookingId  Id of the Booking which should be changed
     * @param newExpense Expense with the new values
     * @return the number of affected rows
     */
    int updateBooking(long bookingId, ExpenseObject newExpense) {

        //TODO update funktionen sollten nur noch die geänderten teile einer buchung ändern und nicht gleich die ganze buchung ändern
        ContentValues values = new ContentValues();
        values.put("price", newExpense.getUnsignedPrice());
        long categoryId = newExpense.getCategory().getIndex();
        values.put("category_id", categoryId);
        values.put("expenditure", newExpense.getExpenditure());
        values.put("title", newExpense.getTitle());
        values.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(newExpense.getDate().getTime()));
        values.put("notice", newExpense.getNotice());
        long accountId = newExpense.getAccount().getIndex();
        values.put("account_id", accountId);

        Log.d(TAG, "changed booking " + bookingId);
        return database.update(ExpensesDbHelper.TABLE_BOOKINGS, values, ExpensesDbHelper.BOOKINGS_COL_ID + " = ?", new String[]{"" + bookingId});
    }

    /**
     * Convenience Method for deleting a Booking
     *
     * @param bookingId Id of the Booking which should be deleted
     * @return the number of affected rows
     */
    int deleteBooking(long bookingId) {

        //TODO es müssen auch alle verbindungen (tags, kinder) zu der angegebenen Buchung gelöscht werden
        Log.d(TAG, "deleted booking at index " + bookingId);
        return database.delete(ExpensesDbHelper.TABLE_BOOKINGS, ExpensesDbHelper.BOOKINGS_COL_ID + " = ?", new String[]{"" + bookingId});
    }

    int deleteBookings(long bookingIds[]) {

        int affectedRows = 0;

        for (long bookingId : bookingIds) {

            deleteBooking(bookingId);
            affectedRows++;
        }

        return affectedRows;
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
        values.put("price", child.getUnsignedPrice());
        values.put("booking_id", parentId);
        values.put("category_id", child.getCategory().getIndex());
        values.put("expenditure", child.getExpenditure());
        values.put("title", child.getTitle());
        values.put("date", child.getDBDate());
        values.put("notice", child.getNotice());
        values.put("account_id", child.getAccount().getIndex());

        for (String tag : child.getTags()) {

            //TODO
            //assignTagToBooking(expense.getIndex(), tag);
        }

        Log.d(TAG, "created expense at Child_Booking table");
        return database.insert(ExpensesDbHelper.TABLE_CHILD_BOOKINGS, null, values);
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
    long createChildBooking(ExpenseObject childExpense, long parentId) {

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
            return -1;
        }
    }

    long createChildBooking(List<ExpenseObject> childs) {

        long parentId = createDummyExpense();

        for (ExpenseObject child : childs) {

            createChildBooking(child, parentId);
        }

        return parentId;
    }

    ArrayList<ExpenseObject> getChildsToParent(long parentId) {

        ArrayList<ExpenseObject> childBookings = new ArrayList<>();

        String selectQuery;

        selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_CHILD_BOOKINGS;
        selectQuery += " WHERE " + ExpensesDbHelper.CHILD_BOOKINGS_COL_PARENT_BOOKING_ID + " = '" + parentId;
        selectQuery += "' ORDER BY " + ExpensesDbHelper.BOOKINGS_COL_DATE + " ASC;";

        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        if (!c.isAfterLast()) {

            while (!c.isAfterLast()) {

                childBookings.add(cursorToExpense(c));
                c.moveToNext();
            }
        }

        return childBookings;
    }

    ExpenseObject getChildBookingById(long index) {

        String selectQuery;

        selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_CHILD_BOOKINGS;
        selectQuery += " WHERE " + ExpensesDbHelper.CHILD_BOOKINGS_COL_PARENT_BOOKING_ID + " = " + index + ";";

        Log.d(TAG, "getChildBookingById: " + selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        return c.isAfterLast() ? new ExpenseObject() : cursorToExpense(c);
    }

    boolean isChild(long expenseId) {

        String selectQuery;

        selectQuery = "SELECT COUNT(1) 'exists' FROM " + ExpensesDbHelper.TABLE_CHILD_BOOKINGS;
        selectQuery += " WHERE " + ExpensesDbHelper.CHILD_BOOKINGS_COL_ID;
        selectQuery += " = " + expenseId + ";";

        Log.d(TAG, "isChild: " + selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        c.moveToFirst();


        return c.getInt(c.getColumnIndex("exists")) != 0;
    }

    int updateChildBooking(long childId, ExpenseObject updatedChild) {

        ContentValues values = new ContentValues();
        values.put("price", updatedChild.getUnsignedPrice());

        //TODO child booking kann momentan nicht einem neuen parent zugewiesen werden, sondern nur der content der booking geändert werden

        values.put("category_id", updatedChild.getCategory().getIndex());
        values.put("expenditure", updatedChild.getExpenditure());
        values.put("title", updatedChild.getTitle());
        values.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(updatedChild.getDate().getTime()));
        values.put("notice", updatedChild.getNotice());
        values.put("account_id", updatedChild.getAccount().getIndex());

        Log.d(TAG, "changed child booking " + childId);
        return database.update(ExpensesDbHelper.TABLE_CHILD_BOOKINGS, values, ExpensesDbHelper.CHILD_BOOKINGS_COL_ID + " = ?", new String[]{"" + childId});
    }

    int deleteChildBooking(long childId) {

        //TODO wenn das kind das letzte des parents war muss der parent wieder als normale buchung eingefügt werden
        Log.d(TAG, "deleted child booking at index " + childId);
        return database.delete(ExpensesDbHelper.TABLE_CHILD_BOOKINGS, ExpensesDbHelper.CHILD_BOOKINGS_COL_ID + " = ?", new String[]{"" + childId});
    }


    long createCategory(String categoryName, int color) {


        //TODO erstelle neue Kategorie wenn sie nicht bereits existiert
        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.CATEGORIES_COL_NAME, categoryName);
        values.put(ExpensesDbHelper.CATEGORIES_COL_COLOR, color);
        Log.d(TAG, "created new category " + categoryName);

        return database.insert(ExpensesDbHelper.TABLE_CATEGORIES, null, values);
    }

    ArrayList<Category> getAllCategories() {

        ArrayList<Category> allCategories = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_CATEGORIES;
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, DatabaseUtils.dumpCursorToString(c));

        c.moveToFirst();
/* überarbeitung 24.11.17
        if (!c.isAfterLast()) {

            while (!c.isAfterLast()) {


                long index = c.getLong(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_ID));
                String categoryName = c.getString(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_NAME));
                int color = c.getInt(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_COLOR));

                allCategories.add(new Category(index, categoryName, color));
                c.moveToNext();
            }
        }
*/
        while(!c.isAfterLast()) {

            long index = c.getLong(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_ID));
            String categoryName = c.getString(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_NAME));
            int color = c.getInt(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_COLOR));

            allCategories.add(new Category(index, categoryName, color));
            c.moveToNext();
        }


        return allCategories;
    }

    /**
     * Convenience Method for getting a Category by its name
     *
     * @param category Name of the category
     * @return Returns an Category object
     */
    Category getCategoryByName(String category) {

        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_CATEGORIES + " WHERE " + ExpensesDbHelper.CATEGORIES_COL_NAME + " = \"" + category + "\"";
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();
        Category category1 = new Category();

        if (!c.isAfterLast()) {

            category1.setIndex(c.getLong(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_ID)));
            category1.setCategoryName(c.getString(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_NAME)));
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
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);

        Log.d(TAG, DatabaseUtils.dumpCursorToString(c));

        Category category = new Category();

        c.moveToFirst();

        if (!c.isAfterLast()) {

            category.setIndex(c.getLong(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_ID)));
            category.setCategoryName(c.getString(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_NAME)));
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
    int updateCategoryColor(int color, long categoryId) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.CATEGORIES_COL_COLOR, color);
        values.put(ExpensesDbHelper.CATEGORIES_COL_NAME, getCategoryById(categoryId).getCategoryName());

        Log.d(TAG, "update category " + categoryId);

        return database.update(ExpensesDbHelper.TABLE_CATEGORIES, values, ExpensesDbHelper.CATEGORIES_COL_ID + " = ?", new String[]{"" + categoryId});
    }

    /**
     * Convenience Method for updating a Categories color
     *
     * @param categoryName new name of the category
     * @param categoryId   Id of the Category which should be changed
     * @return The id of the affected row
     */
    int updateCategoryName(String categoryName, long categoryId) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.CATEGORIES_COL_COLOR, getCategoryById(categoryId).getColor());
        values.put(ExpensesDbHelper.CATEGORIES_COL_NAME, categoryName);

        Log.d(TAG, "update category " + categoryId);

        return database.update(ExpensesDbHelper.TABLE_CATEGORIES, values, ExpensesDbHelper.CATEGORIES_COL_ID + " = ?", new String[]{"" + categoryId});
    }

    /**
     * Convenience Method for deleting a Category by id
     *
     * @param categoryId Id of the Category which should be deleted
     * @return The result of the deleting operation
     */
    int deleteCategory(long categoryId) {

        //TODO kategorien können nicht gelöscht werden, wenn es noch buchungen gibt, die in dieser Kategorie gemacht wurden
        Log.d(TAG, "delete Category + " + categoryId);
        return database.delete(ExpensesDbHelper.TABLE_CATEGORIES, ExpensesDbHelper.CATEGORIES_COL_ID + " = ?", new String[]{"" + categoryId});
    }


    long createTemplateBooking(long templateBookingId) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.TEMPLATE_COL_F_BOOKING_ID, templateBookingId);
        Log.d(TAG, "createTemplateBooking: " + templateBookingId);

        return database.insert(ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS, null, values);
    }

    ArrayList<ExpenseObject> getTemplates() {

        ArrayList<ExpenseObject> allTemplates = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS;
        Log.d(TAG, "getTemplates: " + selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getTemplates: " + DatabaseUtils.dumpCursorToString(c));

        c.moveToFirst();

        if (!c.isAfterLast()) {

            while (!c.isAfterLast()) {

                long index = c.getLong(c.getColumnIndex(ExpensesDbHelper.TEMPLATE_COL_F_BOOKING_ID));
                allTemplates.add(getBookingById(index));

                c.moveToNext();
            }
        }

        return allTemplates;
    }

    ExpenseObject getTemplate(long index) {

        String selectQuery = "SELECT * FROM "
                + ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS
                + " WHERE "
                + ExpensesDbHelper.TEMPLATE_COL_F_BOOKING_ID
                + " = "
                + index;
        Log.d(TAG, "getTemplate: " + selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getTemplate: " + DatabaseUtils.dumpCursorToString(c));

        c.moveToFirst();

        if (!c.isAfterLast()) {

            return getBookingById(c.getLong(c.getColumnIndex(ExpensesDbHelper.TEMPLATE_COL_F_BOOKING_ID)));
        } else {

            return new ExpenseObject();
        }
    }

    int updateTemplate(long index, ExpenseObject newTemplate) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.TEMPLATE_COL_F_BOOKING_ID, newTemplate.getIndex());
        Log.d(TAG, "updateTemplate: " + newTemplate.getIndex());

        return database.update(ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS, values, ExpensesDbHelper.TEMPLATE_COL_ID + " = ?", new String[]{"" + index});
    }

    int deleteTemplate(long index) {

        Log.d(TAG, "deleteTemplate: " + index);
        return database.delete(ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS, ExpensesDbHelper.TEMPLATE_COL_ID + " = ?", new String[]{"" + index});
    }


    long createRecurringBooking(long recurringBookingId, Calendar start, int frequency, Calendar end) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_F_BOOKING_ID, recurringBookingId);
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_START, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(start.getTime()));
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY, frequency);
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_END, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(end.getTime()));
        Log.d(TAG, "createRecurringBooking: " + recurringBookingId);

        return database.insert(ExpensesDbHelper.TABLE_RECURRING_BOOKINGS, null, values);
    }

    ArrayList<ExpenseObject> getRecurringBookings(Calendar dateRngStart, Calendar endDate) {//TODO nicht ganz zufrieden mit der funktion, bitte überdenken

        ArrayList<ExpenseObject> allRecurringBookings = new ArrayList<>();

        //exclude all events which end before the given date range
        String selectQuery = "SELECT * FROM "
                + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
                + " WHERE "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_END
                + " > '"
                + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(dateRngStart.getTime())
                + "';";
        Log.d(TAG, "getRecurringBookings: " + selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getRecurringBookings: " + DatabaseUtils.dumpCursorToString(c));

        c.moveToFirst();
        ExpenseObject expense;
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

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

                    expense = getBookingById(c.getLong(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_F_BOOKING_ID)));
                    expense.setDate(start);
                    allRecurringBookings.add(expense);
                }

                start.add(Calendar.HOUR, frequency);
            }
            c.moveToNext();
        }

        return allRecurringBookings;
    }

    ExpenseObject getRecurringBooking(long index) {

        String selectQuery = "SELECT * FROM "
                + ExpensesDbHelper.TABLE_RECURRING_BOOKINGS
                + " WHERE "
                + ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID
                + " = "
                + index;
        Log.d(TAG, "getRecurringBooking: " + selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getRecurringBooking: " + DatabaseUtils.dumpCursorToString(c));

        c.moveToFirst();

        if (!c.isAfterLast()) {

            return getBookingById(c.getLong(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID)));
        } else {

            return new ExpenseObject();
        }
    }

    int updateRecurringBooking(ExpenseObject newRecurringBooking, String startDate, int frequency, String endDate, long index) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_F_BOOKING_ID, newRecurringBooking.getIndex());
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_START, startDate);
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY, frequency);
        values.put(ExpensesDbHelper.RECURRING_BOOKINGS_COL_END, endDate);
        Log.d(TAG, "updateRecurringBooking: " + index);

        return database.update(ExpensesDbHelper.TABLE_RECURRING_BOOKINGS, values, ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + " = ?", new String[]{"" + index});
    }

    int deleteRecurringBooking(long index) {

        Log.d(TAG, "deleteRecurringBooking: " + index);
        return database.delete(ExpensesDbHelper.TABLE_RECURRING_BOOKINGS, ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID + "= ?", new String[]{"" + index});
    }
}
