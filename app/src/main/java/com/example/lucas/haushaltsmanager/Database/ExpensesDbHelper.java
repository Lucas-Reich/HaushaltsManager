package com.example.lucas.haushaltsmanager.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.Migrations.IMigration;
import com.example.lucas.haushaltsmanager.Database.Migrations.MigrationHelper;

public class ExpensesDbHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "expenses.db";
    public static final int DB_VERSION = 5;
    public static final int INVALID_INDEX = -1;

    //define table Template_Bookings
    public static final String TABLE_TEMPLATE_BOOKINGS = "TEMPLATE_BOOKINGS";

    public static final String TEMPLATE_COL_ID = "template_id";
    public static final String TEMPLATE_COL_BOOKING_ID = "booking_id";

    //define table Recurring Bookings
    public static final String TABLE_RECURRING_BOOKINGS = "RECURRING_BOOKINGS";

    public static final String RECURRING_BOOKINGS_COL_ID = "recurring_booking__id"; // TODO: rename field in Database
    public static final String RECURRING_BOOKINGS_COL_BOOKING_ID = "booking_id";
    public static final String RECURRING_BOOKINGS_COL_OCCURRENCE = "start";
    @Deprecated
    public static final String RECURRING_BOOKINGS_COL_FREQUENCY = "frequency"; // Deprecated in Migration2
    public static final String RECURRING_BOOKINGS_COL_END = "end";
    public static final String RECURRING_BOOKINGS_COL_CALENDAR_FIELD = "calendar_field"; // Created in Migration2
    public static final String RECURRING_BOOKINGS_COL_AMOUNT = "amount"; // Created in Migration2


    // define table Bookings
    public static final String TABLE_BOOKINGS = "BOOKINGS";

    public static final String BOOKINGS_COL_ID = "booking_id";
    public static final String BOOKINGS_COL_CREATED_AT = "created_at";
    public static final String BOOKINGS_COL_EXPENSE_TYPE = "expense_type";
    public static final String BOOKINGS_COL_PRICE = "price";
    public static final String BOOKINGS_COL_CATEGORY_ID = "category_id";
    public static final String BOOKINGS_COL_EXPENDITURE = "expenditure";
    public static final String BOOKINGS_COL_TITLE = "title";
    public static final String BOOKINGS_COL_DATE = "date";
    public static final String BOOKINGS_COL_NOTICE = "notice";
    public static final String BOOKINGS_COL_ACCOUNT_ID = "account_id";
    public static final String BOOKINGS_COL_CURRENCY_ID = "currency_id";
    public static final String BOOKINGS_COL_HIDDEN = "hidden";
    public static final String BOOKINGS_COL_PARENT_ID = "parent_id";


    // define table Accounts
    public static final String TABLE_ACCOUNTS = "ACCOUNTS";

    public static final String ACCOUNTS_COL_ID = "account_id";
    public static final String ACCOUNTS_COL_NAME = "acc_name";
    public static final String ACCOUNTS_COL_BALANCE = "balance";
    public static final String ACCOUNTS_COL_CURRENCY_ID = "currency_id";


    // define table Tags
    @Deprecated
    public static final String TABLE_TAGS = "TAGS";

    @Deprecated
    public static final String TAGS_COL_ID = "tag_id";
    @Deprecated
    public static final String TAGS_COL_NAME = "tag_name";

    //define table parentCategories
    public static final String TABLE_CATEGORIES = "CATEGORIES";

    public static final String CATEGORIES_COL_ID = "category_id";
    public static final String CATEGORIES_COL_NAME = "name";
    public static final String CATEGORIES_COL_COLOR = "color";
    public static final String CATEGORIES_COL_DEFAULT_EXPENSE_TYPE = "default_expense_type";

    // define table ChildCategories
    public static final String TABLE_CHILD_CATEGORIES = "CHILD_CATEGORIES";

    public static final String CHILD_CATEGORIES_COL_ID = "child_category_id";
    public static final String CHILD_CATEGORIES_COL_NAME = "name";
    public static final String CHILD_CATEGORIES_COL_COLOR = "color";
    public static final String CHILD_CATEGORIES_COL_HIDDEN = "hidden";
    public static final String CHILD_CATEGORIES_COL_PARENT_ID = "parent_id";
    public static final String CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE = "default_expense_type";


    // define table Booking_Tags
    @Deprecated
    public static final String TABLE_BOOKINGS_TAGS = "BOOKING_TAGS";

    @Deprecated
    public static final String BOOKINGS_TAGS_COL_ID = "booking_tag_id";
    @Deprecated
    public static final String BOOKINGS_TAGS_COL_TAG_ID = "tag_id";
    @Deprecated
    public static final String BOOKINGS_TAGS_COL_BOOKING_ID = "booking_id";


    //defining table Currencies
    public static final String TABLE_CURRENCIES = "CURRENCIES";

    public static final String CURRENCIES_COL_ID = "currency_id";
    public static final String CURRENCIES_COL_CREATED_AT = "created_at";
    public static final String CURRENCIES_COL_SYMBOL = "symbol";
    public static final String CURRENCIES_COL_NAME = "cur_name";
    public static final String CURRENCIES_COL_SHORT_NAME = "short_name";


    public ExpensesDbHelper() {
        super(app.getContext(), DB_NAME, null, DB_VERSION);
    }

    public ExpensesDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        IMigration[] migrations = MigrationHelper.getMigrations();

        for (IMigration migration : migrations) {
            migration.apply(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        IMigration[] migrations = MigrationHelper.getMigrations();

        for (int i = oldVersion; i < newVersion; i++) {
            migrations[i].apply(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        IMigration[] migrations = MigrationHelper.getMigrations();

        for (int i = oldVersion; i > newVersion; i--) {
            migrations[i - 1].revert(db);
        }
    }
}
