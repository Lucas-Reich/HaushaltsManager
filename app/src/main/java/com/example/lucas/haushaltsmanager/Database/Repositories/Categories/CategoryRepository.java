package com.example.lucas.haushaltsmanager.Database.Repositories.Categories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions.CategoryNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryRepository;
import com.example.lucas.haushaltsmanager.Entities.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {
    // REFACTOR: Versuchen das Repo so wie das TagRepo zu bauen
    private SQLiteDatabase mDatabase;

    public CategoryRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
    }

    public boolean exists(Category category) {
        String selectQuery;

        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_CATEGORIES
                + " WHERE " + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_ID + " = " + category.getIndex()
                + " AND " + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_NAME + " = '" + category.getTitle() + "'"
                + " AND " + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_COLOR + " = '" + category.getColorString() + "'"
                + " AND " + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE + " = " + (category.getDefaultExpenseType() ? 1 : 0)
                + " LIMIT 1;";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    public Category get(long categoryId) throws CategoryNotFoundException {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CATEGORIES_COL_ID + ", "
                + ExpensesDbHelper.CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE
                + " FROM " + ExpensesDbHelper.TABLE_CATEGORIES
                + " WHERE " + ExpensesDbHelper.CATEGORIES_COL_ID + " = " + categoryId + ";";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (!c.moveToFirst()) {
            throw new CategoryNotFoundException(categoryId);
        }

        Category category = cursorToCategory(c);
        category.addChildren(new ChildCategoryRepository(app.getContext()).getAll(category.getIndex()));

        c.close();
        return category;
    }

    public List<Category> getAll() {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CATEGORIES_COL_ID + ", "
                + ExpensesDbHelper.CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE + " "
                + "FROM " + ExpensesDbHelper.TABLE_CATEGORIES + ";";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        c.moveToFirst();
        ArrayList<Category> categories = new ArrayList<>();
        while (!c.isAfterLast()) {

            categories.add(cursorToCategory(c));
            c.moveToNext();
        }

        c.close();

        return categories;
    }

    public Category insert(Category category) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.CATEGORIES_COL_NAME, category.getTitle());
        values.put(ExpensesDbHelper.CATEGORIES_COL_COLOR, category.getColorString());
        values.put(ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE, category.getDefaultExpenseType() ? 1 : 0);

        long insertedCategoryId = mDatabase.insert(ExpensesDbHelper.TABLE_CATEGORIES, null, values);

        Category parentCategory = new Category(
                insertedCategoryId,
                category.getTitle(),
                category.getColorString(),
                category.getDefaultExpenseType(),
                new ArrayList<Category>()
        );

        for (Category childCategory : category.getChildren()) {
            parentCategory.addChild(new ChildCategoryRepository(app.getContext()).insert(parentCategory, childCategory));
        }

        return parentCategory;
    }

    public void update(Category category) throws CategoryNotFoundException {
        ContentValues updatedCategory = new ContentValues();
        updatedCategory.put(ExpensesDbHelper.CATEGORIES_COL_NAME, category.getTitle());
        updatedCategory.put(ExpensesDbHelper.CATEGORIES_COL_COLOR, category.getColorString());
        updatedCategory.put(ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE, (category.getDefaultExpenseType() ? 1 : 0));

        int affectedRows = mDatabase.update(ExpensesDbHelper.TABLE_CATEGORIES, updatedCategory, ExpensesDbHelper.CATEGORIES_COL_ID + " = ?", new String[]{category.getIndex() + ""});

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
                new ChildCategoryRepository(app.getContext()).getAll(categoryIndex)
        );
    }
}