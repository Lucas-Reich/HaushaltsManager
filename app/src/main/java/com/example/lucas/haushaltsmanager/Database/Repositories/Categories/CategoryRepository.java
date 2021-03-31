package com.example.lucas.haushaltsmanager.Database.Repositories.Categories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions.CategoryCouldNotBeCreatedException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions.CategoryNotFoundException;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CategoryRepository implements CategoryRepositoryInterface {
    private final String TABLE = "CATEGORIES";

    private SQLiteDatabase mDatabase;
    private final TransformerInterface<Category> transformer;

    public CategoryRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
        transformer = new CategoryTransformer();
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

    public void insert(Category category) throws CategoryCouldNotBeCreatedException {
        ContentValues values = new ContentValues();
        values.put("id", category.getId().toString());
        values.put("name", category.getTitle());
        values.put("color", category.getColor().getColorString());
        values.put("default_expense_type", category.getDefaultExpenseType().value() ? 1 : 0);
        values.put("hidden", 0);

        try {
            mDatabase.insertOrThrow(
                    TABLE,
                    null,
                    values
            );
        } catch (SQLException e) {
            throw new CategoryCouldNotBeCreatedException(category, e);
        }
    }

    public void update(Category category) throws CategoryNotFoundException {
        ContentValues updatedCategory = new ContentValues();
        updatedCategory.put("name", category.getTitle());
        updatedCategory.put("color", category.getColor().getColorString());
        updatedCategory.put("default_expense_type", (category.getDefaultExpenseType().value() ? 1 : 0));

        int affectedRows = mDatabase.update(
                TABLE,
                updatedCategory,
                "id = ?",
                new String[]{category.getId().toString()}
        );

        if (affectedRows == 0) {
            throw new CategoryNotFoundException(category.getId());
        }
    }

    public void delete(Category category) throws SQLException {
        mDatabase.delete(
                TABLE,
                "id = ?",
                new String[]{category.getId().toString()}
        );
    }

    public Category get(UUID id) throws CategoryNotFoundException {
        Cursor c = executeRaw(new GetCategoryQuery(id));

        if (!c.moveToFirst()) {
            throw new CategoryNotFoundException(id);
        }

        Category category = transformer.transform(c);

        c.close();
        return category;
    }

    private Cursor executeRaw(QueryInterface query) {
        return mDatabase.rawQuery(String.format(
                query.sql(),
                query.values()
        ), null);
    }
}