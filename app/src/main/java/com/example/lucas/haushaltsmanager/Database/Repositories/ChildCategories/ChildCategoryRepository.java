package com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.CategoryRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions.CannotDeleteCategoryException;
import com.example.lucas.haushaltsmanager.Database.Repositories.Categories.Exceptions.CategoryNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.Exceptions.CannotDeleteChildCategoryException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.Exceptions.ChildCategoryNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Category;

import java.util.ArrayList;
import java.util.List;

public class ChildCategoryRepository {

    public static boolean exists(Category category) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery;
        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES
                + " WHERE " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + " = " + category.getIndex()
                + " AND " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME + " = '" + category.getTitle() + "'"
                + " AND " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR + " = '" + category.getColorString() + "'"
                + " AND " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE + " = " + (category.getDefaultExpenseType() ? 1 : 0)
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

    public static Category get(long categoryId) throws ChildCategoryNotFoundException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + ", "
                + ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE
                + " FROM " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES
                + " WHERE " + ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + " = " + categoryId + ";";

        Cursor c = db.rawQuery(selectQuery, null);

        if (!c.moveToFirst()) {
            throw new ChildCategoryNotFoundException(categoryId);
        }

        Category category = cursorToChildCategory(c);

        c.close();
        DatabaseManager.getInstance().closeDatabase();
        return category;
    }

    public static List<Category> getAll(long parentId) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String selectQuery = "SELECT "
                + ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + ", "
                + ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME + ", "
                + ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR + ", "
                + ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE + " "
                + "FROM " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + " "
                + "WHERE " + ExpensesDbHelper.CHILD_CATEGORIES_COL_PARENT_ID + " = '" + parentId + "' "
                + "AND " + ExpensesDbHelper.CHILD_CATEGORIES_COL_HIDDEN + " = '" + 0 + "';";

        Cursor c = db.rawQuery(selectQuery, null);

        c.moveToFirst();
        ArrayList<Category> categories = new ArrayList<>();
        while (!c.isAfterLast()) {

            categories.add(cursorToChildCategory(c));
            c.moveToNext();
        }

        DatabaseManager.getInstance().closeDatabase();
        c.close();

        return categories;
    }

    public static Category insert(Category parentCategory, Category childCategory) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME, childCategory.getTitle());
        values.put(ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR, childCategory.getColorString());
        values.put(ExpensesDbHelper.CHILD_CATEGORIES_COL_HIDDEN, 0);
        values.put(ExpensesDbHelper.CHILD_CATEGORIES_COL_PARENT_ID, parentCategory.getIndex());
        values.put(ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE, childCategory.getDefaultExpenseType() ? 1 : 0);

        long insertedCategoryId = db.insert(ExpensesDbHelper.TABLE_CHILD_CATEGORIES, null, values);
        DatabaseManager.getInstance().closeDatabase();

        return new Category(
                insertedCategoryId,
                childCategory.getTitle(),
                childCategory.getColorString(),
                childCategory.getDefaultExpenseType(),
                childCategory.getChildren()
        );
    }

    public static void delete(Category category) throws CannotDeleteChildCategoryException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        if (isAttachedToParentBooking(category))
            throw CannotDeleteChildCategoryException.childCategoryAttachedToParentExpenseException(category);

        if (isAttachedToChildBooking(category))
            throw CannotDeleteChildCategoryException.childCategoryAttachedToChildExpenseException(category);

        if (isLastChildOfParent(category)) {

            try {

                Category parentCategory = getParent(category);

                db.delete(ExpensesDbHelper.TABLE_CHILD_CATEGORIES, ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + " = ?", new String[]{"" + category.getIndex()});
                deleteParentCategory(parentCategory);

            } catch (CannotDeleteCategoryException e) {
                throw CannotDeleteChildCategoryException.childCategoryParentCannotBeDeleted(category);
            } catch (CategoryNotFoundException e) {
                throw CannotDeleteChildCategoryException.childCategoryParentNotFoundException(category);
            }
        }

        db.delete(ExpensesDbHelper.TABLE_CHILD_CATEGORIES, ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + " = ?", new String[]{"" + category.getIndex()});

        DatabaseManager.getInstance().closeDatabase();
    }

    public static void update(Category category) throws ChildCategoryNotFoundException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ContentValues updatedCategory = new ContentValues();
        updatedCategory.put(ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME, category.getTitle());
        updatedCategory.put(ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR, category.getColorString());
        updatedCategory.put(ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE, category.getDefaultExpenseType());

        int affectedRows = db.update(ExpensesDbHelper.TABLE_CHILD_CATEGORIES, updatedCategory, ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + " = ?", new String[]{"" + category.getIndex()});
        DatabaseManager.getInstance().closeDatabase();

        if (affectedRows == 0)
            throw new ChildCategoryNotFoundException(category.getIndex());
    }

    public static void hide(Category category) throws ChildCategoryNotFoundException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ContentValues updatedCategory = new ContentValues();
        updatedCategory.put(ExpensesDbHelper.CHILD_CATEGORIES_COL_HIDDEN, 1);

        int affectedRows = db.update(ExpensesDbHelper.TABLE_CHILD_CATEGORIES, updatedCategory, ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + " = ?", new String[]{"" + category.getIndex()});
        DatabaseManager.getInstance().closeDatabase();

        if (affectedRows == 0)
            throw new ChildCategoryNotFoundException(category.getIndex());
    }

    private static Category getParent(Category childCategory) throws CategoryNotFoundException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

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

        Cursor c = db.rawQuery(selectQuery, null);

        if (!c.moveToFirst()) {
            throw new CategoryNotFoundException(childCategory.getIndex());
        }

        Category category = CategoryRepository.cursorToCategory(c);

        c.close();
        DatabaseManager.getInstance().closeDatabase();
        return category;
    }

    private static boolean isAttachedToParentBooking(Category category) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery;

        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID + " = " + category.getIndex()
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

    private static boolean isAttachedToChildBooking(Category category) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery;

        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_BOOKINGS
                + " WHERE " + ExpensesDbHelper.TABLE_BOOKINGS + "." + ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID + " = " + category.getIndex()
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

    private static boolean isLastChildOfParent(Category category) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        String subSelectQuery;
        subSelectQuery = "(SELECT"
                + " " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_PARENT_ID
                + " FROM " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES
                + " WHERE " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_ID + " = " + category.getIndex()
                + ");";

        String selectQuery;
        selectQuery = "SELECT"
                + " *"
                + " FROM " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES
                + " WHERE " + ExpensesDbHelper.TABLE_CHILD_CATEGORIES + "." + ExpensesDbHelper.CHILD_CATEGORIES_COL_PARENT_ID + " = " + subSelectQuery
                + ";";

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.getCount() == 1) {

            c.close();
            DatabaseManager.getInstance().closeDatabase();
            return true;
        }

        c.close();
        DatabaseManager.getInstance().closeDatabase();
        return false;
    }

    private static void deleteParentCategory(Category parentCategory) throws CannotDeleteCategoryException {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        if (!isLastChildOfParent(parentCategory))
            throw new CannotDeleteCategoryException(parentCategory);

        db.delete(ExpensesDbHelper.TABLE_CATEGORIES, ExpensesDbHelper.CATEGORIES_COL_ID + " = ?", new String[]{"" + parentCategory.getIndex()});
        DatabaseManager.getInstance().closeDatabase();
    }

    public static Category cursorToChildCategory(Cursor c) {
        long categoryIndex = c.getLong(c.getColumnIndex(ExpensesDbHelper.CHILD_CATEGORIES_COL_ID));
        String categoryName = c.getString(c.getColumnIndex(ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME));
        String categoryColor = c.getString(c.getColumnIndex(ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR));
        boolean defaultExpenseType = c.getInt(c.getColumnIndex(ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE)) == 1;

        return new Category(
                categoryIndex,
                categoryName,
                categoryColor,
                defaultExpenseType,
                new ArrayList<Category>()
        );
    }
}
