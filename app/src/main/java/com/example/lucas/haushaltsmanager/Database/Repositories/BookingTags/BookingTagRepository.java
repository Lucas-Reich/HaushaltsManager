package com.example.lucas.haushaltsmanager.Database.Repositories.BookingTags;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Tags.TagRepository;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Tag;

import java.util.ArrayList;
import java.util.List;

public class BookingTagRepository {

    /**
     * Methode um alle Tags zu einer Buchung zu bekommen.
     *
     * @param expenseId   Id der Buchung
     * @param expenseType Ausgabentyp der Buchung (Um Herauszufinden ob die Buchung ein Kind ist oder nicht)
     * @return Alle Tags die zu der angegebenen Buchung gehören
     */
    public static List<Tag> get(long expenseId, ExpenseObject.EXPENSE_TYPES expenseType) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery;
        selectQuery = "SELECT "
                + ExpensesDbHelper.TABLE_TAGS + "." + ExpensesDbHelper.TAGS_COL_ID + ", "
                + ExpensesDbHelper.TABLE_TAGS + "." + ExpensesDbHelper.TAGS_COL_NAME
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS_TAGS
                + " JOIN " + ExpensesDbHelper.TABLE_TAGS + " ON " + ExpensesDbHelper.TABLE_BOOKINGS_TAGS + "." + ExpensesDbHelper.BOOKINGS_TAGS_COL_TAG_ID + " = " + ExpensesDbHelper.TABLE_TAGS + "." + ExpensesDbHelper.TAGS_COL_ID
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS_TAGS + "." + ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_ID + " = " + expenseId
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS_TAGS + "." + ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_TYPE + " = '" + expenseType + "';";

        Cursor c = db.rawQuery(selectQuery, null);

        c.moveToFirst();

        List<Tag> tags = new ArrayList<>();
        while (!c.isAfterLast()) {

            tags.add(TagRepository.cursorToTag(c));
            c.moveToNext();
        }

        c.close();
        DatabaseManager.getInstance().closeDatabase();

        return tags;
    }


    /**
     * Methode um ein Tag zu einer Buchung hinzuzufügen.
     *
     * @param bookingId Id der Buchung zu der das Tag hinzugefügt werden soll
     * @param tag       Tag welcher der angegebenen Buchung hinzugefügt werden soll
     */
    public static void insert(long bookingId, Tag tag, ExpenseObject.EXPENSE_TYPES expenseType) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.BOOKINGS_TAGS_COL_TAG_ID, tag.getIndex());
        values.put(ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_ID, bookingId);
        values.put(ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_TYPE, expenseType.name());

        db.insert(ExpensesDbHelper.TABLE_BOOKINGS_TAGS, null, values);
        DatabaseManager.getInstance().closeDatabase();
    }

    /**
     * Methode um ein Tag von einer Buchung zu löschen.
     *
     * @param expense Buchung, von der das Tag entfernt werden soll
     * @param tag     Tag, welches entfernt werden soll
     */
    public static void delete(ExpenseObject expense, Tag tag) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String whereClause;
        whereClause = ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_ID + " = ?"
                + " AND " + ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_TYPE + " = ?"
                + " AND " + ExpensesDbHelper.BOOKINGS_TAGS_COL_TAG_ID + " = ?";
        String[] whereArgs = new String[]{
                "" + expense.getIndex(),
                expense.getExpenseType().name(),
                "" + tag.getIndex()
        };

        db.delete(ExpensesDbHelper.TABLE_BOOKINGS_TAGS, whereClause, whereArgs);
        DatabaseManager.getInstance().closeDatabase();
    }

    /**
     * Methode um alle Tags von einer Buchung zu entfernen.
     *
     * @param expense     Buchung von der alle Tags entfernt werden sollen
     * @param expenseType Ausgabentyp
     */
    public static void deleteAll(ExpenseObject expense, ExpenseObject.EXPENSE_TYPES expenseType) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String whereClause = ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_ID + " = ?"
                + " AND " + ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_TYPE + " = ?";
        String[] whereArgs = new String[]{expense.getIndex() + "", expenseType.name()};

        db.delete(ExpensesDbHelper.TABLE_BOOKINGS_TAGS, whereClause, whereArgs);
        DatabaseManager.getInstance().closeDatabase();
    }
}
