package com.example.lucas.haushaltsmanager.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Tag;

import java.util.ArrayList;
import java.util.List;

public class TagsRepository {

    private static String TAG = AccountsRepository.class.getSimpleName();

    private Context mContext;
    private ExpensesDbHelper dbHelper;
    private SQLiteDatabase database;

    public TagsRepository(Context context) {

        mContext = context;
        dbHelper = new ExpensesDbHelper(mContext);
    }

    public void open() {

        if (!isOpen())
            database = dbHelper.getWritableDatabase();
        Log.d(TAG, "Opened Tags repository connection");
    }

    public void close() {

        dbHelper.close();
        Log.d(TAG, "Closed Tags repository connection");
    }

    public boolean isOpen() {

        return database != null && database.isOpen();
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
     * Convenience Method for creating a new Tag
     *
     * @param tagName getName of the tag which should be created
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
}
