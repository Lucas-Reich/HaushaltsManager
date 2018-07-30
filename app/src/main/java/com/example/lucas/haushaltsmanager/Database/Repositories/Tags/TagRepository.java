package com.example.lucas.haushaltsmanager.Database.Repositories.Tags;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Tags.Exceptions.CannotDeleteTagException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Tags.Exceptions.TagNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Tag;

import java.util.ArrayList;
import java.util.List;

public class TagRepository {

    public static boolean exists(Tag tag) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery;

        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_TAGS
                + " WHERE " + ExpensesDbHelper.TAGS_COL_NAME + " = '" + tag.getName() + "'"
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

    public static Tag get(long tagId) throws TagNotFoundException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery;
        selectQuery = "SELECT "
                + ExpensesDbHelper.TAGS_COL_ID + ", "
                + ExpensesDbHelper.TAGS_COL_NAME
                + " FROM " + ExpensesDbHelper.TABLE_TAGS
                + " WHERE " + ExpensesDbHelper.TAGS_COL_ID + " = " + tagId + ";";

        Cursor c = db.rawQuery(selectQuery, null);

        if (!c.moveToFirst()) {
            throw new TagNotFoundException(tagId);
        }

        Tag tag = cursorToTag(c);

        c.close();
        DatabaseManager.getInstance().closeDatabase();
        return tag;
    }

    public static List<Tag> getAll() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery = "SELECT "
                + ExpensesDbHelper.TAGS_COL_ID + ", "
                + ExpensesDbHelper.TAGS_COL_NAME
                + " FROM " + ExpensesDbHelper.TABLE_TAGS + ";";

        Cursor c = db.rawQuery(selectQuery, null);

        c.moveToFirst();

        List<Tag> tags = new ArrayList<>();
        while (!c.isAfterLast()) {

            tags.add(cursorToTag(c));
            c.moveToNext();
        }

        c.close();
        DatabaseManager.getInstance().closeDatabase();
        return tags;
    }

    public static Tag insert(Tag tag) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.TAGS_COL_NAME, tag.getName());

        long insertedTagId = db.insert(ExpensesDbHelper.TABLE_TAGS, null, values);
        DatabaseManager.getInstance().closeDatabase();

        return new Tag(
                insertedTagId,
                tag.getName()
        );
    }

    public static void delete(Tag tag) throws CannotDeleteTagException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        if (isAttachedToBooking(tag))
            throw new CannotDeleteTagException(tag);

        db.delete(ExpensesDbHelper.TABLE_TAGS, ExpensesDbHelper.TAGS_COL_ID + " = ?", new String[]{"" + tag.getIndex()});

        DatabaseManager.getInstance().closeDatabase();
    }

    public static void update(Tag tag) throws TagNotFoundException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ContentValues updatedTag = new ContentValues();
        updatedTag.put(ExpensesDbHelper.TAGS_COL_NAME, tag.getName());

        int affectedRows = db.update(ExpensesDbHelper.TABLE_TAGS, updatedTag, ExpensesDbHelper.TAGS_COL_ID + " = ?", new String[]{tag.getIndex() + ""});
        DatabaseManager.getInstance().closeDatabase();

        if (affectedRows == 0)
            throw new TagNotFoundException(tag.getIndex());
    }

    private static boolean isAttachedToBooking(Tag tag) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery;
        selectQuery = "SELECT "
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS_TAGS
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS_TAGS + "." + ExpensesDbHelper.BOOKINGS_TAGS_COL_TAG_ID + " = " + tag.getIndex()
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

    public static Tag cursorToTag(Cursor c) {
        long tagIndex = c.getLong(c.getColumnIndex(ExpensesDbHelper.TAGS_COL_ID));
        String tagName = c.getString(c.getColumnIndex(ExpensesDbHelper.TAGS_COL_NAME));

        return new Tag(tagIndex, tagName);
    }

}