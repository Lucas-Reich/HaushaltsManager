package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class ExpensesDbHelper extends SQLiteOpenHelper {

    private static final String TAG = ExpensesDbHelper.class.getSimpleName();

    private static final String DB_NAME = "expenses.db";
    private static final int DB_VERSION = 1;

    // define table Bookings
    static final String TABLE_BOOKINGS = "BOOKINGS";

    static final String BOOKINGS_COL_BOOKING_ID = "_id";
    static final String BOOKINGS_COL_PRICE = "price";
    static final String BOOKINGS_COL_F_CATEGORY_ID = "f_category_id";
    static final String BOOKINGS_COL_EXPENDITURE = "expenditure";
    static final String BOOKINGS_COL_TITLE = "title";
    static final String BOOKINGS_COL_DATE = "date";
    static final String BOOKINGS_COL_NOTICE = "notice";
    static final String BOOKINGS_COL_F_ACCOUNT_ID = "f_account_id";

    private static final String CREATE_BOOKINGS = "CREATE TABLE " + TABLE_BOOKINGS
            + "("
            + BOOKINGS_COL_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + BOOKINGS_COL_PRICE + " REAL NOT NULL, "
            + BOOKINGS_COL_F_CATEGORY_ID + " INTEGER NOT NULL, "
            + BOOKINGS_COL_EXPENDITURE + " INTEGER NOT NULL, "
            + BOOKINGS_COL_TITLE + " TEXT NOT NULL, "
            + BOOKINGS_COL_DATE + " TEXT NOT NULL, "
            + BOOKINGS_COL_NOTICE + " TEXT, "
            + BOOKINGS_COL_F_ACCOUNT_ID + " INTEGER NOT NULL"
            + ");";


    // define table Child_Bookings
    static final String TABLE_CHILD_BOOKINGS = "CHILD_BOOKINGS";

    static final String CHILD_BOOKINGS_COL_BOOKING_ID = "_id";
    static final String CHILD_BOOKINGS_COL_F_PARENT_BOOKING_ID = "f_booking_id";
    static final String CHILD_BOOKINGS_COL_PRICE = "price";
    static final String CHILD_BOOKINGS_COL_F_CATEGORY_ID = "f_category_id";
    static final String CHILD_BOOKINGS_COL_EXPENDITURE = "expenditure";
    static final String CHILD_BOOKINGS_COL_TITLE = "title";
    static final String CHILD_BOOKINGS_COL_DATE = "date";
    static final String CHILD_BOOKINGS_COL_NOTICE = "notice";
    static final String CHILD_BOOKINGS_COL_F_ACCOUNT_ID = "f_account_id";

    private static final String CREATE_CHILD_BOOKINGS = "CREATE TABLE " + TABLE_CHILD_BOOKINGS
            + "("
            + CHILD_BOOKINGS_COL_BOOKING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CHILD_BOOKINGS_COL_F_PARENT_BOOKING_ID + " INTEGER NOT NULL, "
            + CHILD_BOOKINGS_COL_PRICE + " REAL NOT NULL, "
            + CHILD_BOOKINGS_COL_F_CATEGORY_ID + " INTEGER NOT NULL, "
            + CHILD_BOOKINGS_COL_EXPENDITURE + " INTEGER NOT NULL, "
            + CHILD_BOOKINGS_COL_TITLE + " TEXT NOT NULL, "
            + CHILD_BOOKINGS_COL_DATE + " TEXT NOT NULL, "
            + CHILD_BOOKINGS_COL_NOTICE + " TEXT, "
            + CHILD_BOOKINGS_COL_F_ACCOUNT_ID + " INTEGER NOT NULL"
            + ");";


    // define table Accounts
    static final String TABLE_ACCOUNTS = "ACCOUNTS";

    static final String ACCOUNTS_COL_ID = "_id";
    static final String ACCOUNTS_COL_ACCOUNT = "account_name";
    static final String ACCOUNTS_COL_BALANCE = "balance";

    private static final String CREATE_ACCOUNTS = "CREATE TABLE " + TABLE_ACCOUNTS
            + "("
            + ACCOUNTS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ACCOUNTS_COL_ACCOUNT + " TEXT NOT NULL, "
            + ACCOUNTS_COL_BALANCE + " INTEGER"
            + ");";


    // define table Tags
    static final String TABLE_TAGS = "TAGS";

    static final String TAGS_COL_ID = "_id";
    static final String TAGS_COL_TAG_NAME = "tag_name";

    private static final String CREATE_TAGS = "CREATE TABLE " + TABLE_TAGS
            + "("
            + TAGS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TAGS_COL_TAG_NAME + " TEXT NOT NULL"
            + ");";


    // define table Categories
    static final String TABLE_CATEGORIES = "CATEGORIES";

    static final String CATEGORIES_COL_ID = "_id";
    static final String CATEGORIES_COL_CATEGORY_NAME = "category_name";
    static final String CATEGORIES_COL_COLOR = "color";

    private final static String CREATE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES
            + "("
            + CATEGORIES_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CATEGORIES_COL_CATEGORY_NAME + " TEXT NOT NULL, "
            + CATEGORIES_COL_COLOR + " INTEGER NOT NULL"
            + ");";


    // define table Booking_Tags
    static final String TABLE_BOOKINGS_TAGS = "BOOKING_TAGS";

    static final String BOOKINGS_TAGS_COL_ID = "_id";
    static final String BOOKINGS_TAGS_COL_F_BOOKING_ID = "f_booking_id";
    static final String BOOKINGS_TAGS_COL_F_TAG_ID = "f_tag_id";

    private static final String CREATE_BOOKINGS_TAGS = "CREATE TABLE " + TABLE_BOOKINGS_TAGS
            + "("
            + BOOKINGS_TAGS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + BOOKINGS_TAGS_COL_F_BOOKING_ID + " INTEGER NOT NULL, "
            + BOOKINGS_TAGS_COL_F_TAG_ID + " INTEGER NOT NULL "
            + ");";


    ExpensesDbHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
        Log.d(TAG, "DbHelper hat die Datenbank " + getDatabaseName() + " erzeugt");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {

            Log.d(TAG, "Die Tabelle Bookings wird mit SQL-Befehl: " + CREATE_BOOKINGS + " angelegt.");
            db.execSQL(CREATE_BOOKINGS);

            Log.d(TAG, "Die Tabelle Child_Bookings wird mit dem SQL-Befehl: " + CREATE_CHILD_BOOKINGS + " angelegt.");
            db.execSQL(CREATE_CHILD_BOOKINGS);

            Log.d(TAG, "Die Tabelle Accounts wird mit SQL-Befehl: " + CREATE_ACCOUNTS + " angelegt.");
            db.execSQL(CREATE_ACCOUNTS);

            Log.d(TAG, "Die Tabelle Tags wird mit SQL-Befehl: " + CREATE_TAGS + " angelegt.");
            db.execSQL(CREATE_TAGS);

            Log.d(TAG, "Die Tabelle Categories wird mit SQL-Befehl: " + CREATE_CATEGORIES + " angelegt.");
            db.execSQL(CREATE_CATEGORIES);

            Log.d(TAG, "Die Tabelle Bookings_Tags wird mit SQL-Befehl: " + CREATE_BOOKINGS_TAGS + " angelegt.");
            db.execSQL(CREATE_BOOKINGS_TAGS);
        } catch (Exception ex) {

            Log.e(TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHILD_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS_TAGS);

        onCreate(db);
    }
}
