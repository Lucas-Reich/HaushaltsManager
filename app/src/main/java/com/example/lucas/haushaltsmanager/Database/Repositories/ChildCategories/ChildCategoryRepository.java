package com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryTransformer;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions.CategoryNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.Exceptions.CannotDeleteChildCategoryException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.Exceptions.ChildCategoryNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseType;

import java.util.ArrayList;
import java.util.List;

public class ChildCategoryRepository implements ChildCategoryRepositoryInterface {
    private SQLiteDatabase mDatabase;
    private final ChildCategoryTransformer transformer;
    private final CategoryTransformer categoryTransformer;

    public ChildCategoryRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
        transformer = new ChildCategoryTransformer();
        categoryTransformer = new CategoryTransformer(this);
    }

    public Category get(long categoryId) throws ChildCategoryNotFoundException {
        Cursor c = executeRaw(new GetChildCategoryQuery(categoryId));

        if (!c.moveToFirst()) {
            throw new ChildCategoryNotFoundException(categoryId);
        }

        Category category = transformer.transform(c);

        c.close();
        return category;
    }

    public List<Category> getAll(long parentId) {
        Cursor c = executeRaw(new GetAllChildCategoriesQuery(parentId));

        c.moveToFirst();
        ArrayList<Category> categories = new ArrayList<>();
        while (!c.isAfterLast()) {

            categories.add(transformer.transform(c));
            c.moveToNext();
        }

        c.close();

        return categories;
    }

    public Category insert(Category parentCategory, Category childCategory) {

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME, childCategory.getTitle());
        values.put(ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR, childCategory.getColor().getColorString());
        values.put(ExpensesDbHelper.CHILD_CATEGORIES_COL_HIDDEN, 0);
        values.put(ExpensesDbHelper.CHILD_CATEGORIES_COL_PARENT_ID, parentCategory.getIndex());
        values.put(ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE, childCategory.getDefaultExpenseType().value() ? 1 : 0);

        long insertedCategoryId = mDatabase.insert(ExpensesDbHelper.TABLE_CHILD_CATEGORIES, null, values);

        return new Category(
                insertedCategoryId,
                childCategory.getTitle(),
                childCategory.getColor(),
                ExpenseType.load(childCategory.getDefaultExpenseType().value()),
                childCategory.getChildren()
        );
    }

    public void delete(Category category) throws CannotDeleteChildCategoryException {
        try {
            Category parentCategory = getParent(category);

            mDatabase.delete(ExpensesDbHelper.TABLE_CHILD_CATEGORIES, ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + " = ?", new String[]{"" + category.getIndex()});

            if (!hasParentChildren(parentCategory)) {
                // TODO: This should be moved into a TransactionRepository
                deleteParentCategory(parentCategory);
            }

        } catch (CategoryNotFoundException e) {
            throw CannotDeleteChildCategoryException.childCategoryParentNotFoundException(category);
        } catch (SQLException e) {
            throw new CannotDeleteChildCategoryException("Failed to delete ChildCategory", e);
        }
    }

    public void update(Category category) throws ChildCategoryNotFoundException {

        ContentValues updatedCategory = new ContentValues();
        updatedCategory.put(ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME, category.getTitle());
        updatedCategory.put(ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR, category.getColor().getColorString());
        updatedCategory.put(ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE, category.getDefaultExpenseType().value());

        int affectedRows = mDatabase.update(ExpensesDbHelper.TABLE_CHILD_CATEGORIES, updatedCategory, ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + " = ?", new String[]{"" + category.getIndex()});

        if (affectedRows == 0) {
            throw new ChildCategoryNotFoundException(category.getIndex());
        }
    }

    public void hide(Category category) throws ChildCategoryNotFoundException {

        ContentValues updatedCategory = new ContentValues();
        updatedCategory.put(ExpensesDbHelper.CHILD_CATEGORIES_COL_HIDDEN, 1);

        int affectedRows = mDatabase.update(ExpensesDbHelper.TABLE_CHILD_CATEGORIES, updatedCategory, ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + " = ?", new String[]{"" + category.getIndex()});

        if (affectedRows == 0)
            throw new ChildCategoryNotFoundException(category.getIndex());
    }

    private Cursor executeRaw(QueryInterface query) {
        return mDatabase.rawQuery(String.format(
                query.sql(),
                query.values()
        ), null);
    }

    private Category getParent(Category childCategory) throws CategoryNotFoundException {
        Cursor c = executeRaw(new GetParentCategoryQuery(childCategory));

        if (!c.moveToFirst()) {
            throw new CategoryNotFoundException(childCategory.getIndex());
        }

        Category category = categoryTransformer.transform(c);

        c.close();
        return category;
    }

    private boolean hasParentChildren(Category parentCategory) {
        Cursor c = executeRaw(new HasCategoryChildrenQuery(parentCategory));

        if (c.getCount() >= 1) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    private void deleteParentCategory(Category parentCategory) {
        mDatabase.delete(
                ExpensesDbHelper.TABLE_CATEGORIES,
                ExpensesDbHelper.CATEGORIES_COL_ID + " = ?",
                new String[]{"" + parentCategory.getIndex()}
        );
    }
}
