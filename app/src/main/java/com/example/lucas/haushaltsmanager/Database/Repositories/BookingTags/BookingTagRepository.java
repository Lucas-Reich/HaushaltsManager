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
     * Methode um zu überprüfen ob es die Relation zwischen Buchung und Tag existiert.
     *
     * @param expense Buchung, zu der das angegebene Tag zuegprdnet sein soll
     * @param tag     Tag, welches der Buchung zugeprdnet sein soll
     * @return TRUE wenn das Tag der Buchung zugeordnet ist, FALSE wenn nicht
     */
    public static boolean exists(ExpenseObject expense, Tag tag) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery;
        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS_TAGS
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS_TAGS + "." + ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_ID + " = " + expense.getIndex()
                + " AND " + ExpensesDbHelper.TABLE_BOOKINGS_TAGS + "." + ExpensesDbHelper.BOOKINGS_TAGS_COL_TAG_ID + " = " + tag.getIndex()
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
                + ";";

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
        //todo ich muss überprüfen ob es das Tag auch wirklich gibt
        values.put(ExpensesDbHelper.BOOKINGS_TAGS_COL_TAG_ID, tag.getIndex());

        //todo ich muss überprüfen ob es die Buchung auch wirklich gibt
        values.put(ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_ID, bookingId);

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
                + " AND " + ExpensesDbHelper.BOOKINGS_TAGS_COL_TAG_ID + " = ?";
        String[] whereArgs = new String[]{
                "" + expense.getIndex(),
                "" + tag.getIndex()
        };

        db.delete(ExpensesDbHelper.TABLE_BOOKINGS_TAGS, whereClause, whereArgs);
        DatabaseManager.getInstance().closeDatabase();
    }

    /**
     * Methode um alle Tags von einer Buchung zu entfernen.
     *
     * @param expense Buchung von der alle Tags entfernt werden sollen
     */
    public static void deleteAll(ExpenseObject expense) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        db.delete(ExpensesDbHelper.TABLE_BOOKINGS_TAGS, ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_ID + " = ?", new String[]{expense.getIndex() + ""});
        DatabaseManager.getInstance().closeDatabase();
    }
}
