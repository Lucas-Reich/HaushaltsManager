package com.example.lucas.haushaltsmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ExpensesDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = ExpensesDbHelper.class.getSimpleName();

    static final String DB_NAME = "expenses.db";
    static final int DB_VERSION = 1;

    // define table Bookings
    public static final String TABLE_BOOKINGS = "BOOKINGS";

    public static final String BOOKINGS_COL_BOOKING_ID = "_id";
    public static final String BOOKINGS_COL_PRICE = "price";
    public static final String BOOKINGS_COL_F_CATEGORY_ID = "f_category_id";
    public static final String BOOKINGS_COL_EXPENDITURE = "expenditure";
    public static final String BOOKINGS_COL_TITLE = "title";
    public static final String BOOKINGS_COL_DATE = "date";
    public static final String BOOKINGS_COL_NOTICE = "notice";
    public static final String BOOKINGS_COL_F_ACCOUNT_ID = "f_account_id";

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


    // define table Accounts
    public static final String TABLE_ACCOUNTS = "ACCOUNTS";

    public static final String ACCOUNTS_COL_ID = "_id";
    public static final String ACCOUNTS_COL_ACCOUNT = "account_name";

    private static final String CREATE_ACCOUNTS = "CREATE TABLE " + TABLE_ACCOUNTS
            + "("
            + ACCOUNTS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ACCOUNTS_COL_ACCOUNT + " TEXT NOT NULL"
            + ");";


    // define table Tags
    public static final String TABLE_TAGS = "TAGS";

    public static final String TAGS_COL_ID = "_id";
    public static final String TAGS_COL_TAG_NAME = "tag_name";

    private static final String CREATE_TAGS = "CREATE TABLE " + TABLE_TAGS
            + "("
            + TAGS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TAGS_COL_TAG_NAME + " TEXT NOT NULL"
            + ");";


    // define table Categories
    public static final String TABLE_CATEGORIES = "CATEGORIES";

    public static final String CATEGORIES_COL_ID = "_id";
    public static final String CATEGORIES_COL_CATEGORY_NAME = "category_name";
    public static final String CATEGORIES_COL_CATEGORY_SUB_NAME = "category_sub_name";
    public static final String CATEGORIES_COL_COLOR = "color";

    private final static String CREATE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES
            + "("
            + CATEGORIES_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CATEGORIES_COL_CATEGORY_NAME + " TEXT NOT NULL, "
            + CATEGORIES_COL_CATEGORY_SUB_NAME + " TEXT NOT NULL, "
            + CATEGORIES_COL_COLOR + " INTEGER NOT NULL"
            + ");";


    // define table Booking_Tags
    public static final String TABLE_BOOKINGS_TAGS = "BOOKING_TAGS";

    public static final String BOOKINGS_TAGS_COL_ID = "_id";
    public static final String BOOKINGS_TAGS_COL_F_BOOKING_ID = "f_booking_id";
    public static final String BOOKINGS_TAGS_COL_F_TAG_ID = "f_tag_id";

    private static final String CREATE_BOOKINGS_TAGS = "CREATE TABLE " + TABLE_BOOKINGS_TAGS
            + "("
            + BOOKINGS_TAGS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + BOOKINGS_TAGS_COL_F_BOOKING_ID + " INTEGER NOT NULL, "
            + BOOKINGS_TAGS_COL_F_TAG_ID + " INTEGER NOT NULL "
            + ");";


    public ExpensesDbHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank " + getDatabaseName() + " erzeugt");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {

            Log.d(LOG_TAG, "Die Tabelle Bookings wird mit SQL-Befehl: " + CREATE_BOOKINGS + " angelegt.");
            db.execSQL(CREATE_BOOKINGS);

            Log.d(LOG_TAG, "Die Tabelle Accounts wird mit SQL-Befehl: " + CREATE_ACCOUNTS + " angelegt.");
            db.execSQL(CREATE_ACCOUNTS);

            Log.d(LOG_TAG, "Die Tabelle Tags wird mit SQL-Befehl: " + CREATE_TAGS + " angelegt.");
            db.execSQL(CREATE_TAGS);

            Log.d(LOG_TAG, "Die Tabelle Categories wird mit SQL-Befehl: " + CREATE_CATEGORIES + " angelegt.");
            db.execSQL(CREATE_CATEGORIES);

            Log.d(LOG_TAG, "Die Tabelle Bookings_Tags wird mit SQL-Befehl: " + CREATE_BOOKINGS_TAGS + " angelegt.");
            db.execSQL(CREATE_BOOKINGS_TAGS);
        } catch (Exception ex) {

            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS_TAGS);

        onCreate(db);
    }
}
