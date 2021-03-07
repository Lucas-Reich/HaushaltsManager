package com.example.lucas.haushaltsmanager.Database.Repositories.Categories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions.CategoryNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryRepository;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseType;

import java.util.ArrayList;
import java.util.List;

public class CategoryRepository implements CategoryRepositoryInterface {
    private SQLiteDatabase mDatabase;
    private final TransformerInterface<Category> transformer;

    public CategoryRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
        transformer = new CategoryTransformer(new ChildCategoryRepository(context));
    }

    // TODO: This method is only used within tests
    public boolean exists(Category category) {
        Cursor c = executeRaw(new CategoryExistsQuery(category));

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    public List<Category> getAll() {
        Cursor c = executeRaw(new GetAllCategoriesQuery());

        c.moveToFirst();
        ArrayList<Category> categories = new ArrayList<>();
        while (!c.isAfterLast()) {

            categories.add(transformer.transform(c));
            c.moveToNext();
        }

        c.close();

        return categories;
    }

    public Category insert(Category category) {
        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.CATEGORIES_COL_NAME, category.getTitle());
        values.put(ExpensesDbHelper.CATEGORIES_COL_COLOR, category.getColor().getColorString());
        values.put(ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE, category.getDefaultExpenseType().value() ? 1 : 0);

        long insertedCategoryId = mDatabase.insert(ExpensesDbHelper.TABLE_CATEGORIES, null, values);

        Category parentCategory = new Category(
                insertedCategoryId,
                category.getTitle(),
                category.getColor(),
                ExpenseType.load(category.getDefaultExpenseType().value()),
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
        updatedCategory.put(ExpensesDbHelper.CATEGORIES_COL_COLOR, category.getColor().getColorString());
        updatedCategory.put(ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE, (category.getDefaultExpenseType().value() ? 1 : 0));

        int affectedRows = mDatabase.update(ExpensesDbHelper.TABLE_CATEGORIES, updatedCategory, ExpensesDbHelper.CATEGORIES_COL_ID + " = ?", new String[]{category.getIndex() + ""});

        if (affectedRows == 0)
            throw new CategoryNotFoundException(category.getIndex());
    }

    private Cursor executeRaw(QueryInterface query) {
        return mDatabase.rawQuery(String.format(
                query.sql(),
                query.values()
        ), null);
    }
}