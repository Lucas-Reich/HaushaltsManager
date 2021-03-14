package com.example.lucas.haushaltsmanager.Database.Migrations;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CATEGORIES_COL_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CHILD_CATEGORIES_COL_HIDDEN;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CHILD_CATEGORIES_COL_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CHILD_CATEGORIES_COL_PARENT_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_CATEGORIES;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_CHILD_CATEGORIES;

final class V4__Add_Foreign_Keys_To_Child_Categories implements IMigration {
    private static final String TAG = V4__Add_Foreign_Keys_To_Child_Categories.class.getSimpleName();
    private static final String TABLE_CHILD_CATEGORIES_NEW = "CHILD_CATEGORIES_NEW";

    private static final String CREATE_CHILD_CATEGORIES_TABLE_FOREIGN_KEY = "CREATE TABLE " + TABLE_CHILD_CATEGORIES_NEW
            + "("
            + CHILD_CATEGORIES_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CHILD_CATEGORIES_COL_NAME + " TEXT NOT NULL, "
            + CHILD_CATEGORIES_COL_COLOR + " TEXT NOT NULL, "
            + CHILD_CATEGORIES_COL_HIDDEN + " INTEGER NOT NULL, "
            + CHILD_CATEGORIES_COL_PARENT_ID + " INTEGER, "
            + CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE + " INTEGER NOT NULL, "
            + "FOREIGN KEY (" + CHILD_CATEGORIES_COL_PARENT_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + CATEGORIES_COL_ID + ") ON UPDATE CASCADE ON DELETE RESTRICT"
            + ");";

    @Override
    public void apply(SQLiteDatabase db) {
        createChildCategoryTableWithForeignKeys(db);

        copyDataFromOldTable(db);

        deleteOldChildCategoriesTable(db);

        renameNewChildCategoriesTable(db);
    }

    private void createChildCategoryTableWithForeignKeys(SQLiteDatabase db) {
        Log.d(TAG, "Creating new ChildCategories table with foreign key support");
        db.execSQL(CREATE_CHILD_CATEGORIES_TABLE_FOREIGN_KEY);
    }

    private void copyDataFromOldTable(SQLiteDatabase db) {
        Log.d(TAG, "Copying data from old ChildCategories table to new");

        // Copy normal Categories
        db.execSQL(String.format("INSERT INTO %s SELECT * FROM %s WHERE parent_id != -1;",
                TABLE_CHILD_CATEGORIES_NEW,
                TABLE_CHILD_CATEGORIES
        ));

        // Copy hidden Transfer Category
        db.execSQL(String.format("INSERT INTO %s (child_category_id, name, color, hidden, default_expense_type) SELECT child_category_id, name, color, hidden, default_expense_type FROM %s WHERE parent_id = -1;",
                TABLE_CHILD_CATEGORIES_NEW,
                TABLE_CHILD_CATEGORIES
        ));
    }

    private void deleteOldChildCategoriesTable(SQLiteDatabase db) {
        Log.d(TAG, "Deleting old ChildCategories table");

        db.execSQL(String.format("DROP TABLE %s",
                TABLE_CHILD_CATEGORIES
        ));
    }

    private void renameNewChildCategoriesTable(SQLiteDatabase db) {
        Log.d(TAG, "Renaming new ChildCategories table");

        db.execSQL(String.format("ALTER TABLE %s RENAME TO %s;",
                TABLE_CHILD_CATEGORIES_NEW,
                TABLE_CHILD_CATEGORIES
        ));
    }
}
