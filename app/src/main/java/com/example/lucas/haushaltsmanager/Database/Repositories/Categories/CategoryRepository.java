package com.example.lucas.haushaltsmanager.Database.Repositories.Categories;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions.CannotDeleteCategoryException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions.CategoryNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryRepository;
import com.example.lucas.haushaltsmanager.Entities.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {

    public static boolean exists(Category category) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery;

        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_CATEGORIES
                + " WHERE " + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_ID + " = " + category.getIndex()
                + " AND " + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_NAME + " = '" + category.getTitle() + "'"
                + " AND " + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_COLOR + " = '" + category.getColorString() + "'"
                + " AND " + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE + " = " + (category.getDefaultExpenseType() ? 1 : 0)
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

    public static Category get(long categoryId) throws CategoryNotFoundException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CATEGORIES_COL_ID + ", "
                + ExpensesDbHelper.CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE
                + " FROM " + ExpensesDbHelper.TABLE_CATEGORIES
                + " WHERE " + ExpensesDbHelper.CATEGORIES_COL_ID + " = " + categoryId + ";";

        Cursor c = db.rawQuery(selectQuery, null);

        if (!c.moveToFirst()) {
            throw new CategoryNotFoundException(categoryId);
        }

        Category category = cursorToCategory(c);
        category.addChildren(ChildCategoryRepository.getAll(category.getIndex()));

        c.close();
        DatabaseManager.getInstance().closeDatabase();
        return category;
    }

    public static List<Category> getAll() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CATEGORIES_COL_ID + ", "
                + ExpensesDbHelper.CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE + " "
                + "FROM " + ExpensesDbHelper.TABLE_CATEGORIES + ";";

        Cursor c = db.rawQuery(selectQuery, null);

        c.moveToFirst();
        ArrayList<Category> categories = new ArrayList<>();
        while (!c.isAfterLast()) {

            categories.add(cursorToCategory(c));
            c.moveToNext();
        }

        c.close();
        DatabaseManager.getInstance().closeDatabase();

        return categories;
    }

    public static Category insert(Category category) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.CATEGORIES_COL_NAME, category.getTitle());
        values.put(ExpensesDbHelper.CATEGORIES_COL_COLOR, category.getColorString());
        values.put(ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE, category.getDefaultExpenseType() ? 1 : 0);

        long insertedCategoryId = db.insert(ExpensesDbHelper.TABLE_CATEGORIES, null, values);
        DatabaseManager.getInstance().closeDatabase();

        Category parentCategory = new Category(
                insertedCategoryId,
                category.getTitle(),
                category.getColorString(),
                category.getDefaultExpenseType(),
                new ArrayList<Category>()
        );

        for (Category childCategory : category.getChildren()) {
            parentCategory.addChild(ChildCategoryRepository.insert(parentCategory, childCategory));
        }

        return parentCategory;
    }

    public static void update(Category category) throws CategoryNotFoundException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ContentValues updatedCategory = new ContentValues();
        updatedCategory.put(ExpensesDbHelper.CATEGORIES_COL_NAME, category.getTitle());
        updatedCategory.put(ExpensesDbHelper.CATEGORIES_COL_COLOR, category.getColorString());
        updatedCategory.put(ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE, (category.getDefaultExpenseType() ? 1 : 0));

        int affectedRows = db.update(ExpensesDbHelper.TABLE_CATEGORIES, updatedCategory, ExpensesDbHelper.CATEGORIES_COL_ID + " = ?", new String[]{category.getIndex() + ""});
        DatabaseManager.getInstance().closeDatabase();

        if (affectedRows == 0)
            throw new CategoryNotFoundException(category.getIndex());
    }

    public static Category cursorToCategory(Cursor c) {
        long categoryIndex = c.getLong(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_ID));
        String categoryName = c.getString(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_NAME));
        String categoryColor = c.getString(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_COLOR));
        boolean defaultExpenseType = c.getInt(c.getColumnIndex(ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE)) == 1;

        return new Category(
                categoryIndex,
                categoryName,
                categoryColor,
                defaultExpenseType,
                ChildCategoryRepository.getAll(categoryIndex)
        );
    }
}