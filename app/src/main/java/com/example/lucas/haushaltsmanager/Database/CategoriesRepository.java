package com.example.lucas.haushaltsmanager.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Entities.Category;

import java.util.ArrayList;

public class CategoriesRepository {

    private static String TAG = AccountsRepository.class.getSimpleName();

    private Context mContext;
    private ExpensesDbHelper dbHelper;
    private SQLiteDatabase database;

    public CategoriesRepository(Context context) {

        mContext = context;
        dbHelper = new ExpensesDbHelper(mContext);
    }

    public void open() {

        if (!isOpen())
            database = dbHelper.getWritableDatabase();
        Log.d(TAG, "Opened Categories repository connection");
    }

    public void close() {

        dbHelper.close();
        Log.d(TAG, "Closed Categories repository connection");
    }

    public boolean isOpen() {

        return database != null && database.isOpen();
    }


    /**
     * Method for mapping an Cursor to a Category object
     *
     * @param c mDatabase cursor
     * @return Category object
     */
    @NonNull
    private Category cursorToCategory(Cursor c) {

        long categoryIndex = c.getLong(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_ID));
        String categoryName = c.getString(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_NAME));
        String categoryColor = c.getString(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_COLOR));
        boolean defaultExpenseType = c.getInt(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_EXPENSE_TYPE)) == 1;

        return new Category(categoryIndex, categoryName, categoryColor, defaultExpenseType);
    }

    public long createCategory(Category category) {

        //TODO erstelle neue Kategorie wenn sie nicht bereits existiert
        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.CATEGORIES_COL_NAME, category.getCategoryName());
        values.put(ExpensesDbHelper.CATEGORIES_COL_COLOR, category.getColor());
        values.put(ExpensesDbHelper.CATEGORIES_COL_EXPENSE_TYPE, category.getDefaultExpenseType() ? 1 : 0);
        Log.d(TAG, "created new CATEGORY");

        return database.insert(ExpensesDbHelper.TABLE_CATEGORIES, null, values);
    }

    public ArrayList<Category> getAllCategories() {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CATEGORIES_COL_ID + ", "
                + ExpensesDbHelper.CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.CATEGORIES_COL_EXPENSE_TYPE
                + " FROM " + ExpensesDbHelper.TABLE_CATEGORIES + ";";
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        c.moveToFirst();

        ArrayList<Category> allCategories = new ArrayList<>();
        Log.d(TAG, "getAllCategories: " + DatabaseUtils.dumpCursorToString(c));
        while (!c.isAfterLast()) {

            allCategories.add(cursorToCategory(c));
            c.moveToNext();
        }

        return allCategories;
    }

    /**
     * Convenience Method for getting a Category by its name
     *
     * @param category Name of the CATEGORY
     * @return Returns an Category object
     */
    @Nullable
    public Category getCategoryByName(String category) {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CATEGORIES_COL_ID + ", "
                + ExpensesDbHelper.CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.CATEGORIES_COL_EXPENSE_TYPE
                + " FROM " + ExpensesDbHelper.TABLE_TAGS
                + " WHERE " + ExpensesDbHelper.CATEGORIES_COL_NAME + " = '" + category + "';";
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getCategoryByName: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        return c.isAfterLast() ? null : cursorToCategory(c);
    }

    /**
     * Convenience Method for getting a Category
     *
     * @param categoryId index of the desired Category
     * @return Returns an Category object
     */
    public Category getCategoryById(long categoryId) {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CATEGORIES_COL_ID + ", "
                + ExpensesDbHelper.CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.CATEGORIES_COL_EXPENSE_TYPE
                + " FROM " + ExpensesDbHelper.TABLE_TAGS
                + " WHERE " + ExpensesDbHelper.CATEGORIES_COL_ID + " = " + categoryId + ";";
        Log.d(TAG, selectQuery);

        Cursor c = database.rawQuery(selectQuery, null);
        Log.d(TAG, "getCategoryById: " + DatabaseUtils.dumpCursorToString(c));
        c.moveToFirst();

        return c.isAfterLast() ? null : cursorToCategory(c);
    }

    /**
     * Convenience Method for updating a Categories color
     *
     * @param categoryId   Id of the Category which should be changed
     * @param categoryName new name of the CATEGORY
     * @return The id of the affected row
     */
    public int updateCategoryName(long categoryId, String categoryName) {

        throw new UnsupportedOperationException("Updating Categories us not Supported");//todo
    }

    /**
     * Convenience Method for deleting a Category by id
     *
     * @param categoryId Id of the Category which should be deleted
     * @return The result of the deleting operation
     */
    public int deleteCategory(long categoryId) {

        //TODO kategorien können nicht gelöscht werden, wenn es noch buchungen gibt, die in dieser Kategorie gemacht wurden
        Log.d(TAG, "delete Category + " + categoryId);
        return database.delete(ExpensesDbHelper.TABLE_CATEGORIES, ExpensesDbHelper.CATEGORIES_COL_ID + " = ?", new String[]{"" + categoryId});
    }
}
