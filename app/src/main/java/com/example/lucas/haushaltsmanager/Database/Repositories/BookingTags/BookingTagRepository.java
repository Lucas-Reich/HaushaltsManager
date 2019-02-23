package com.example.lucas.haushaltsmanager.Database.Repositories.BookingTags;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Tags.TagRepository;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Tag;

import java.util.ArrayList;
import java.util.List;

public class BookingTagRepository {
    private SQLiteDatabase mDatabase;

    public BookingTagRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
    }

    /**
     * Methode um zu überprüfen ob es die Relation zwischen Buchung und Tag existiert.
     *
     * @param expense Buchung, zu der das angegebene Tag zuegprdnet sein soll
     * @param tag     Tag, welches der Buchung zugeprdnet sein soll
     * @return TRUE wenn das Tag der Buchung zugeordnet ist, FALSE wenn nicht
     */
    public boolean exists(ExpenseObject expense, Tag tag) {

        String selectQuery;
        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS_TAGS
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS_TAGS + "." + ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_ID + " = " + expense.getIndex()
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS_TAGS + "." + ExpensesDbHelper.BOOKINGS_TAGS_COL_TAG_ID + " = " + tag.getIndex()
                + " LIMIT 1;";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }


    /**
     * Methode um zuerfahren ob der angegebene Tag zu einer Buchung hinzugefügt wurde
     *
     * @param tag Zu überprüfendes Tag
     * @return TRUE wenn es Buchungen mit diesem Tag gibt, FALSE wenn nicht
     */
    public boolean isTagAssignedToBooking(Tag tag) {
        String selectQuery = "SELECT "
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS_TAGS
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS_TAGS + "." + ExpensesDbHelper.BOOKINGS_TAGS_COL_TAG_ID + " = " + tag.getIndex()
                + " LIMIT 1;";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    /**
     * Methode um alle Tags zu einer Buchung zu bekommen.
     *
     * @param expenseId Id der Buchung
     * @return Alle Tags die zu der angegebenen Buchung gehören
     */
    public List<Tag> get(long expenseId) {

        String selectQuery;
        selectQuery = "SELECT "
                + ExpensesDbHelper.TABLE_TAGS + "." + ExpensesDbHelper.TAGS_COL_ID + ", "
                + ExpensesDbHelper.TABLE_TAGS + "." + ExpensesDbHelper.TAGS_COL_NAME
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS_TAGS
                + " JOIN " + ExpensesDbHelper.TABLE_TAGS + " ON " + ExpensesDbHelper.TABLE_BOOKINGS_TAGS + "." + ExpensesDbHelper.BOOKINGS_TAGS_COL_TAG_ID + " = " + ExpensesDbHelper.TABLE_TAGS + "." + ExpensesDbHelper.TAGS_COL_ID
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS_TAGS + "." + ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_ID + " = " + expenseId
                + ";";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        c.moveToFirst();

        List<Tag> tags = new ArrayList<>();
        while (!c.isAfterLast()) {

            tags.add(TagRepository.fromCursor(c));
            c.moveToNext();
        }

        c.close();

        return tags;
    }


    /**
     * Methode um ein Tag zu einer Buchung hinzuzufügen.
     *
     * @param bookingId Id der Buchung zu der das Tag hinzugefügt werden soll
     * @param tag       Tag welcher der angegebenen Buchung hinzugefügt werden soll
     */
    public void insert(long bookingId, Tag tag) {

        ContentValues values = new ContentValues();
        // TODO: Ich muss überprüfen ob es das Tag auch wirklich gibt.
        values.put(ExpensesDbHelper.BOOKINGS_TAGS_COL_TAG_ID, tag.getIndex());

        // TODO: Ich muss überprüfen ob es die Buchung auch wirklich gibt.
        values.put(ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_ID, bookingId);

        mDatabase.insert(ExpensesDbHelper.TABLE_BOOKINGS_TAGS, null, values);
    }

    /**
     * Methode um ein Tag von einer Buchung zu löschen.
     *
     * @param expense Buchung, von der das Tag entfernt werden soll
     * @param tag     Tag, welches entfernt werden soll
     */
    public void delete(ExpenseObject expense, Tag tag) {

        String whereClause;
        whereClause = ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_ID + " = ?"
                + " AND " + ExpensesDbHelper.BOOKINGS_TAGS_COL_TAG_ID + " = ?";
        String[] whereArgs = new String[]{
                "" + expense.getIndex(),
                "" + tag.getIndex()
        };

        mDatabase.delete(ExpensesDbHelper.TABLE_BOOKINGS_TAGS, whereClause, whereArgs);
    }

    /**
     * Methode um alle Tags von einer Buchung zu entfernen.
     *
     * @param expense Buchung von der alle Tags entfernt werden sollen
     */
    public void deleteAll(ExpenseObject expense) {

        mDatabase.delete(ExpensesDbHelper.TABLE_BOOKINGS_TAGS, ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_ID + " = ?", new String[]{expense.getIndex() + ""});
    }
}
