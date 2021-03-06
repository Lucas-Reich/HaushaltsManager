package com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryRepository;
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

    public ChildCategoryRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
        transformer = new ChildCategoryTransformer();
    }

    public boolean exists(Category category) {

        String selectQuery;
        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES
                + " WHERE " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + " = " + category.getIndex()
                + " AND " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME + " = '" + category.getTitle() + "'"
                + " AND " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR + " = '" + category.getColor().getColorString() + "'"
                + " AND " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE + " = " + (category.getDefaultExpenseType().value() ? 1 : 0)
                + " LIMIT 1;";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    public Category get(long categoryId) throws ChildCategoryNotFoundException {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + ", "
                + ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE
                + " FROM " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES
                + " WHERE " + ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + " = " + categoryId + ";";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (!c.moveToFirst()) {
            throw new ChildCategoryNotFoundException(categoryId);
        }

        Category category = transformer.transform(c);

        c.close();
        return category;
    }

    public List<Category> getAll(long parentId) {

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + ", "
                + ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE + " "
                + "FROM " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + " "
                + "WHERE " + ExpensesDbHelper.CHILD_CATEGORIES_COL_PARENT_ID + " = '" + parentId + "' "
                + "AND " + ExpensesDbHelper.CHILD_CATEGORIES_COL_HIDDEN + " = '" + 0 + "';";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

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

        if (isAttachedToParentBooking(category))
            throw CannotDeleteChildCategoryException.childCategoryAttachedToParentExpenseException(category);

        if (isAttachedToChildBooking(category))
            throw CannotDeleteChildCategoryException.childCategoryAttachedToChildExpenseException(category);

        try {
            Category parentCategory = getParent(category);

            mDatabase.delete(ExpensesDbHelper.TABLE_CHILD_CATEGORIES, ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + " = ?", new String[]{"" + category.getIndex()});

            // TODO: Wenn der Parent nicht gelöscht werden konnte und eine Exception ausgelöst wurde,
            //  ist an dieser Stelle die KindKategorie bereits gelöscht.
            //  Man müsste beides also in einer Transaktion laufen lassen.

            if (!hasParentChildren(parentCategory)) {
                deleteParentCategory(parentCategory);
            }

        } catch (CategoryNotFoundException e) {
            throw CannotDeleteChildCategoryException.childCategoryParentNotFoundException(category);
        }
    }

    public void update(Category category) throws ChildCategoryNotFoundException {

        ContentValues updatedCategory = new ContentValues();
        updatedCategory.put(ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME, category.getTitle());
        updatedCategory.put(ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR, category.getColor().getColorString());
        updatedCategory.put(ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE, category.getDefaultExpenseType().value());

        int affectedRows = mDatabase.update(ExpensesDbHelper.TABLE_CHILD_CATEGORIES, updatedCategory, ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + " = ?", new String[]{"" + category.getIndex()});

        if (affectedRows == 0)
            throw new ChildCategoryNotFoundException(category.getIndex());
    }

    public void hide(Category category) throws ChildCategoryNotFoundException {

        ContentValues updatedCategory = new ContentValues();
        updatedCategory.put(ExpensesDbHelper.CHILD_CATEGORIES_COL_HIDDEN, 1);

        int affectedRows = mDatabase.update(ExpensesDbHelper.TABLE_CHILD_CATEGORIES, updatedCategory, ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + " = ?", new String[]{"" + category.getIndex()});

        if (affectedRows == 0)
            throw new ChildCategoryNotFoundException(category.getIndex());
    }

    private Category getParent(Category childCategory) throws CategoryNotFoundException {

        String subQuery;
        subQuery = "(SELECT "
                + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_PARENT_ID
                + " FROM " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES
                + " WHERE " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + " = '" + childCategory.getIndex() + "'"
                + ")";


        String selectQuery;
        selectQuery = "SELECT "
                + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_ID + ", "
                + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE
                + " FROM " + ExpensesDbHelper.TABLE_CATEGORIES
                + " WHERE " + ExpensesDbHelper.TABLE_CATEGORIES + "." + ExpensesDbHelper.CATEGORIES_COL_ID + " = " + subQuery + ";";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (!c.moveToFirst()) {
            throw new CategoryNotFoundException(childCategory.getIndex());
        }

        Category category = CategoryRepository.cursorToCategory(c);

        c.close();
        return category;
    }

    private boolean isAttachedToParentBooking(Category category) {
        String selectQuery;

        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID + " = " + category.getIndex()
                + " LIMIT 1;";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    private boolean isAttachedToChildBooking(Category category) {
        String selectQuery;

        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID + " = " + category.getIndex()
                + " LIMIT 1;";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {

            c.close();
            return true;
        }

        c.close();
        return false;
    }

    private boolean hasParentChildren(Category parentCategory) {
        String selectQuery = "SELECT *"
                + " FROM " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES
                + " WHERE " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_PARENT_ID + " = " + parentCategory.getIndex()
                + ";";

        Cursor c = mDatabase.rawQuery(selectQuery, null);

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
