package com.example.lucas.haushaltsmanager.Database.Repositories.Tags;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.BaseRepository;
import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.BookingTags.BookingTagRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Tags.Exceptions.CannotDeleteTagException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Tags.Exceptions.TagNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Tag;

import java.util.ArrayList;
import java.util.List;

public class TagRepository implements BaseRepository<Tag> {
    private SQLiteDatabase mDatabase;
    private DatabaseManager mDbManager;
    private BookingTagRepository mBookingTagsRepo;

    public TagRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDbManager = DatabaseManager.getInstance();
        mDatabase = mDbManager.openDatabase();
        mBookingTagsRepo = new BookingTagRepository(context);
    }

    public boolean exists(Tag tag) {
        String selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_TAGS
                + " WHERE " + ExpensesDbHelper.TABLE_TAGS + "." + ExpensesDbHelper.TAGS_COL_ID + " = " + tag.getIndex()
                + " AND " + ExpensesDbHelper.TABLE_TAGS + "." + ExpensesDbHelper.TAGS_COL_NAME + " = '" + tag.getName() + "'"
                + " LIMIT 1;";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    public Tag get(long tagId) throws TagNotFoundException {
        String selectQuery = "SELECT "
                + ExpensesDbHelper.TAGS_COL_ID + ", "
                + ExpensesDbHelper.TAGS_COL_NAME
                + " FROM " + ExpensesDbHelper.TABLE_TAGS
                + " WHERE " + ExpensesDbHelper.TAGS_COL_ID + " = " + tagId + ";";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (!c.moveToFirst())
            throw new TagNotFoundException(tagId);


        Tag tag = fromCursor(c);

        c.close();
        return tag;
    }

    public List<Tag> getAll() {
        String selectQuery = "SELECT "
                + ExpensesDbHelper.TAGS_COL_ID + ", "
                + ExpensesDbHelper.TAGS_COL_NAME
                + " FROM " + ExpensesDbHelper.TABLE_TAGS + ";";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        c.moveToFirst();

        List<Tag> tags = new ArrayList<>();
        while (!c.isAfterLast()) {

            tags.add(fromCursor(c));
            c.moveToNext();
        }

        c.close();
        return tags;
    }

    public Tag create(Tag tag) {
        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.TAGS_COL_NAME, tag.getName());

        long tagId = mDatabase.insert(ExpensesDbHelper.TABLE_TAGS, null, values);

        return new Tag(
                tagId,
                tag.getName()
        );
    }

    public void delete(Tag tag) throws CannotDeleteTagException {
        if (mBookingTagsRepo.isTagAssignedToBooking(tag))
            throw new CannotDeleteTagException(tag);

        mDatabase.delete(ExpensesDbHelper.TABLE_TAGS, ExpensesDbHelper.TAGS_COL_ID + " = ?", new String[]{"" + tag.getIndex()});
    }

    public void update(Tag tag) throws TagNotFoundException {
        ContentValues updatedTag = new ContentValues();
        updatedTag.put(ExpensesDbHelper.TAGS_COL_NAME, tag.getName());

        int affectedRows = mDatabase.update(
                ExpensesDbHelper.TABLE_TAGS,
                updatedTag,
                ExpensesDbHelper.TAGS_COL_ID + " = ?",
                new String[]{tag.getIndex() + ""}
        );

        if (affectedRows == 0)
            throw new TagNotFoundException(tag.getIndex());
    }

    public static Tag fromCursor(Cursor c) {
        // die fromCursor mehthode kann nicht statisch sein, da das BookingTagRepository sonst eine dependency auf das TagRepository hätte
        // und die beiden sich so die ganze Zeit gegenseitig initialisieren würden
        long tagIndex = c.getLong(c.getColumnIndex(ExpensesDbHelper.TAGS_COL_ID));
        String tagName = c.getString(c.getColumnIndex(ExpensesDbHelper.TAGS_COL_NAME));

        return new Tag(tagIndex, tagName);
    }

    public void closeDatabase() {
        mDbManager.closeDatabase();
    }
}