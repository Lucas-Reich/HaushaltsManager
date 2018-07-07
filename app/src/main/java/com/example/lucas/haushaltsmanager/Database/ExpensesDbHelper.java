package com.example.lucas.haushaltsmanager.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;

class ExpensesDbHelper extends SQLiteOpenHelper {
    private static final String TAG = ExpensesDbHelper.class.getSimpleName();

    private Context mContext;

    private static final String DB_NAME = "expenses.db";
    private static final int DB_VERSION = 1;

    //define table Template_Bookings
    static final String TABLE_TEMPLATE_BOOKINGS = "TEMPLATE_BOOKINGS";

    static final String TEMPLATE_COL_ID = "_id";
    static final String TEMPLATE_COL_BOOKING_ID = "booking_id";
    static final String TEMPLATE_COL_BOOKING_TYPE = "booking_type";

    private static final String CREATE_TEMPLATE_BOOKINGS = "CREATE TABLE " + TABLE_TEMPLATE_BOOKINGS
            + "("
            + TEMPLATE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TEMPLATE_COL_BOOKING_ID + " INTEGER NOT NULL, "
            + TEMPLATE_COL_BOOKING_TYPE + " TEXT NOT NULL"
            + ");";

    //define table Recurring Bookings
    static final String TABLE_RECURRING_BOOKINGS = "RECURRING_BOOKINGS";

    static final String RECURRING_BOOKINGS_COL_ID = "_id";
    static final String RECURRING_BOOKINGS_COL_BOOKING_ID = "booking_id";
    static final String RECURRING_BOOKINGS_COL_BOOKING_TYPE = "booking_type";
    static final String RECURRING_BOOKINGS_COL_START = "start";
    static final String RECURRING_BOOKINGS_COL_FREQUENCY = "frequency";
    static final String RECURRING_BOOKINGS_COL_END = "end";

    private static final String CREATE_RECURRING_BOOKINGS = "CREATE TABLE " + TABLE_RECURRING_BOOKINGS
            + "("
            + RECURRING_BOOKINGS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RECURRING_BOOKINGS_COL_BOOKING_ID + " INTEGER NOT NULL, "
            + RECURRING_BOOKINGS_COL_BOOKING_TYPE + " TEXT NOT NULL, "
            + RECURRING_BOOKINGS_COL_START + " TEXT NOT NULL, "
            + RECURRING_BOOKINGS_COL_FREQUENCY + " INTEGER NOT NULL, "
            + RECURRING_BOOKINGS_COL_END + " TEXT NOT NULL"
            + ");";


    // define table Bookings
    static final String TABLE_BOOKINGS = "BOOKINGS";

    static final String BOOKINGS_COL_ID = "_id";
    static final String BOOKINGS_COL_CREATED_AT = "created_at";
    static final String BOOKINGS_COL_EXPENSE_TYPE = "expense_type";
    static final String BOOKINGS_COL_PRICE = "price";
    static final String BOOKINGS_COL_CATEGORY_ID = "category_id";
    static final String BOOKINGS_COL_EXPENDITURE = "expenditure";
    static final String BOOKINGS_COL_TITLE = "title";
    static final String BOOKINGS_COL_DATE = "date";
    static final String BOOKINGS_COL_NOTICE = "notice";
    static final String BOOKINGS_COL_ACCOUNT_ID = "account_id";
    static final String BOOKINGS_COL_IS_PARENT = "is_parent";//todo brauchen wir das flag noch, wenn der expense type mit gespeichert wird?

    private static final String CREATE_BOOKINGS = "CREATE TABLE " + TABLE_BOOKINGS
            + "("
            + BOOKINGS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + BOOKINGS_COL_CREATED_AT + " DEFAULT CURRENT_TIMESTAMP, "
            + BOOKINGS_COL_EXPENSE_TYPE + " TEXT NOT NULL, "
            + BOOKINGS_COL_PRICE + " REAL NOT NULL, "
            + BOOKINGS_COL_CATEGORY_ID + " INTEGER NOT NULL, "
            + BOOKINGS_COL_EXPENDITURE + " INTEGER NOT NULL, "
            + BOOKINGS_COL_TITLE + " TEXT NOT NULL, "
            + BOOKINGS_COL_DATE + " TEXT NOT NULL, "
            + BOOKINGS_COL_NOTICE + " TEXT, "
            + BOOKINGS_COL_ACCOUNT_ID + " INTEGER NOT NULL, "
            + BOOKINGS_COL_IS_PARENT + " INTEGER NOT NULL"
            + ");";


    // define table Child_Bookings
    static final String TABLE_CHILD_BOOKINGS = "CHILD_BOOKINGS";

    static final String CHILD_BOOKINGS_COL_ID = "_id";
    static final String CHILD_BOOKINGS_COL_CREATED_AT = "created_at";
    static final String CHILD_BOOKINGS_COL_EXPENSE_TYPE = "expense_type";
    static final String CHILD_BOOKINGS_COL_PARENT_BOOKING_ID = "booking_id";
    static final String CHILD_BOOKINGS_COL_PRICE = "price";
    static final String CHILD_BOOKINGS_COL_CATEGORY_ID = "category_id";
    static final String CHILD_BOOKINGS_COL_EXPENDITURE = "expenditure";
    static final String CHILD_BOOKINGS_COL_TITLE = "title";
    static final String CHILD_BOOKINGS_COL_DATE = "date";
    static final String CHILD_BOOKINGS_COL_NOTICE = "notice";
    static final String CHILD_BOOKINGS_COL_ACCOUNT_ID = "account_id";

    private static final String CREATE_CHILD_BOOKINGS = "CREATE TABLE " + TABLE_CHILD_BOOKINGS
            + "("
            + CHILD_BOOKINGS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CHILD_BOOKINGS_COL_CREATED_AT + " DEFAULT CURRENT_TIMESTAMP, "
            + CHILD_BOOKINGS_COL_EXPENSE_TYPE + " TEXT NOT NULL, "//hinzugefügt 17.5.18
            + CHILD_BOOKINGS_COL_PARENT_BOOKING_ID + " INTEGER NOT NULL, "
            + CHILD_BOOKINGS_COL_PRICE + " REAL NOT NULL, "
            + CHILD_BOOKINGS_COL_CATEGORY_ID + " INTEGER NOT NULL, "
            + CHILD_BOOKINGS_COL_EXPENDITURE + " INTEGER NOT NULL, "
            + CHILD_BOOKINGS_COL_TITLE + " TEXT NOT NULL, "
            + CHILD_BOOKINGS_COL_DATE + " TEXT NOT NULL, "
            + CHILD_BOOKINGS_COL_NOTICE + " TEXT, "
            + CHILD_BOOKINGS_COL_ACCOUNT_ID + " INTEGER NOT NULL"
            + ");";


    // define table Accounts
    static final String TABLE_ACCOUNTS = "ACCOUNTS";

    static final String ACCOUNTS_COL_ID = "_id";
    static final String ACCOUNTS_COL_NAME = "acc_name";
    static final String ACCOUNTS_COL_BALANCE = "balance";
    static final String ACCOUNTS_COL_CURRENCY_ID = "currency_id";

    private static final String CREATE_ACCOUNTS = "CREATE TABLE " + TABLE_ACCOUNTS
            + "("
            + ACCOUNTS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ACCOUNTS_COL_NAME + " TEXT NOT NULL, "
            + ACCOUNTS_COL_BALANCE + " INTEGER, "
            + ACCOUNTS_COL_CURRENCY_ID + " INTEGER NOT NULL"
            + ");";


    // define table Tags
    static final String TABLE_TAGS = "TAGS";

    static final String TAGS_COL_ID = "_id";
    static final String TAGS_COL_NAME = "tag_name";

    private static final String CREATE_TAGS = "CREATE TABLE " + TABLE_TAGS
            + "("
            + TAGS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TAGS_COL_NAME + " TEXT NOT NULL"
            + ");";

    //define table parentCategories
    static final String TABLE_CATEGORIES = "CATEGORIES";

    static final String CATEGORIES_COL_ID = "_id";
    static final String CATEGORIES_COL_NAME = "name";
    static final String CATEGORIES_COL_COLOR = "color";
    static final String CATEGORIES_COL_DEFAULT_EXPENSE_TYPE = "default_expense_type";

    private final static String CREATE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES
            + "("
            + CATEGORIES_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CATEGORIES_COL_NAME + " TEXT NOT NULL, "
            + CATEGORIES_COL_COLOR + " TEXT NOT NULL, "
            + CATEGORIES_COL_DEFAULT_EXPENSE_TYPE + " INTEGER NOT NULL"
            + ");";

    // define table ChildCategories
    static final String TABLE_CHILD_CATEGORIES = "CHILD_CATEGORIES";

    static final String CHILD_CATEGORIES_COL_ID = "_id";
    static final String CHILD_CATEGORIES_COL_NAME = "name";
    static final String CHILD_CATEGORIES_COL_COLOR = "color";
    static final String CHILD_CATEGORIES_COL_HIDDEN = "hidden";
    static final String CHILD_CATEGORIES_COL_PARENT_ID = "parent_id";
    static final String CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE = "default_expense_type";

    private final static String CREATE_CHILD_CATEGORIES = "CREATE TABLE " + TABLE_CHILD_CATEGORIES
            + "("
            + CHILD_CATEGORIES_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CHILD_CATEGORIES_COL_NAME + " TEXT NOT NULL, "
            + CHILD_CATEGORIES_COL_COLOR + " TEXT NOT NULL, "
            + CHILD_CATEGORIES_COL_HIDDEN + " INTEGER NOT NULL, "
            + CHILD_CATEGORIES_COL_PARENT_ID + " INTEGER NOT NULL, "
            + CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE + " INTEGER NOT NULL"
            + ");";


    // define table Booking_Tags
    static final String TABLE_BOOKINGS_TAGS = "BOOKING_TAGS";

    static final String BOOKINGS_TAGS_COL_ID = "_id";
    static final String BOOKINGS_TAGS_COL_TAG_ID = "tag_id";
    static final String BOOKINGS_TAGS_COL_BOOKING_ID = "booking_id";
    static final String BOOKINGS_TAGS_COL_BOOKING_TYPE = "booking_type";

    private static final String CREATE_BOOKINGS_TAGS = "CREATE TABLE " + TABLE_BOOKINGS_TAGS
            + "("
            + BOOKINGS_TAGS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + BOOKINGS_TAGS_COL_TAG_ID + " INTEGER NOT NULL, "
            + BOOKINGS_TAGS_COL_BOOKING_ID + " INTEGER NOT NULL, "
            + BOOKINGS_TAGS_COL_BOOKING_TYPE + " TEXT NOT NULL"
            + ");";


    //defining table Currencies
    static final String TABLE_CURRENCIES = "CURRENCIES";

    static final String CURRENCIES_COL_ID = "_id";
    static final String CURRENCIES_COL_CREATED_AT = "created_at";
    static final String CURRENCIES_COL_SYMBOL = "symbol";
    static final String CURRENCIES_COL_NAME = "cur_name";
    static final String CURRENCIES_COL_SHORT_NAME = "short_name";

    private static final String CREATE_CURRENCIES = "CREATE TABLE " + TABLE_CURRENCIES
            + "("
            + CURRENCIES_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CURRENCIES_COL_CREATED_AT + " DEFAULT CURRENT_TIMESTAMP, "
            + CURRENCIES_COL_SYMBOL + " TEXT, "
            + CURRENCIES_COL_NAME + " TEXT NOT NULL, "
            + CURRENCIES_COL_SHORT_NAME + " TEXT NOT NULL"
            + ");";


    ExpensesDbHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
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

            Log.d(TAG, "Die Tabelle Categories wird mit dem SQL-Befehl: " + CREATE_CATEGORIES + " angelegt.");
            db.execSQL(CREATE_CATEGORIES);

            Log.d(TAG, "Die Tabelle ChildCategories wird mit SQL-Befehl: " + CREATE_CHILD_CATEGORIES + " angelegt.");
            db.execSQL(CREATE_CHILD_CATEGORIES);
            insertHiddenCategories(db);

            Log.d(TAG, "Die Tabelle Bookings_Tags wird mit SQL-Befehl: " + CREATE_BOOKINGS_TAGS + " angelegt.");
            db.execSQL(CREATE_BOOKINGS_TAGS);

            Log.d(TAG, "Die Tabelle Template_Bookings wird mit SQL-Befehl: " + CREATE_TEMPLATE_BOOKINGS + " angelegt.");
            db.execSQL(CREATE_TEMPLATE_BOOKINGS);

            Log.d(TAG, "Die Tabelle Recurring_Bookings wird mit SQL-Befehl: " + CREATE_RECURRING_BOOKINGS + " angelegt.");
            db.execSQL(CREATE_RECURRING_BOOKINGS);

            Log.d(TAG, "Die Tabelle Currencies wird mit SQL-Befehl: " + CREATE_CURRENCIES + " angelegt.");
            db.execSQL(CREATE_CURRENCIES);
            insertCurrencies(db);

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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHILD_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS_TAGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEMPLATE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECURRING_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURRENCIES);

        onCreate(db);
    }

    /**
     * initialization of currency table
     *
     * @param db reference to editable mDatabase
     */
    private void insertCurrencies(SQLiteDatabase db) {

        //details from: https://developers.google.com/public-data/docs/canonical/currencies_csv
        ArrayList<String[]> currencies = new ArrayList<>();
        currencies.add(new String[]{"AUD", "Australian Dollar", "$"});
        currencies.add(new String[]{"BGN", "Bulgarian Lev", "лв"});
        currencies.add(new String[]{"BRL", "Brazilian Real", "R$"});
        currencies.add(new String[]{"CAD", "Canadian Dollar", "$"});
        currencies.add(new String[]{"CHF", "Swiss Franc", "CHF"});
        currencies.add(new String[]{"CNY", "Yuan Reminbi", "¥"});
        currencies.add(new String[]{"CZK", "Czech Koruna", "Kč"});
        currencies.add(new String[]{"DKK", "Danish Krone", "kr"});
        currencies.add(new String[]{"GBP", "Pound Sterling", "£"});
        currencies.add(new String[]{"HKD", "Hong Kong Dollar", "$"});
        currencies.add(new String[]{"HRK", "Croatian Kuna", "kn"});
        currencies.add(new String[]{"HUF", "Forint", "Ft"});
        currencies.add(new String[]{"IDR", "Rupiah", "Rp"});
        currencies.add(new String[]{"ILS", "New Israeli Sheqel", "₪"});
        currencies.add(new String[]{"INR", "Indian Rupee", null});
        currencies.add(new String[]{"JPY", "Yen", "¥"});
        currencies.add(new String[]{"KRW", "Won", "₩"});
        currencies.add(new String[]{"MXN", "Mexican Peso", "$"});
        currencies.add(new String[]{"MYR", "Malaysian Ringgit", "RM"});
        currencies.add(new String[]{"NOK", "Norwegian Krone", "kr"});
        currencies.add(new String[]{"NZD", "New Zealand Dollar", "$"});
        currencies.add(new String[]{"PHP", "Philippine Peso", "Php"});
        currencies.add(new String[]{"PLN", "Zloty", "zł"});
        currencies.add(new String[]{"RON", "Romanian Leu", "lei"});
        currencies.add(new String[]{"RUB", "Russian Ruble", "руб"});
        currencies.add(new String[]{"SEK", "Swedish Krona", "kr"});
        currencies.add(new String[]{"SGD", "Singapore Dollar", "$"});
        currencies.add(new String[]{"THB", "Baht", "฿"});
        currencies.add(new String[]{"TRY", "Turkish Lira", "TL"});
        currencies.add(new String[]{"USD", "US Dollar", "$"});
        currencies.add(new String[]{"ZAR", "Rand", "R"});
        currencies.add(new String[]{"EUR", "Euro", "€"});

        for (String[] entry : currencies) {

            ContentValues values = new ContentValues();
            values.put(CURRENCIES_COL_SHORT_NAME, entry[0]);
            values.put(CURRENCIES_COL_NAME, entry[1]);
            values.put(CURRENCIES_COL_SYMBOL, entry[2]);

            db.insert(TABLE_CURRENCIES, null, values);
        }
    }

    /**
     * Initialisiere System Kategorien.
     *
     * @param db Datenbank
     */
    private void insertHiddenCategories(SQLiteDatabase db) {

        ArrayList<Category> categories = new ArrayList<>();
        categories.add(new Category(
                mContext.getResources().getString(R.string.category_transfer),
                "#" + Integer.toHexString(mContext.getResources().getColor(R.color.transfer_booking_color)),
                true
        ));


        for (Category category : categories) {

            ContentValues values = new ContentValues();
            values.put(CHILD_CATEGORIES_COL_NAME, category.getTitle());
            values.put(CHILD_CATEGORIES_COL_COLOR, category.getColorString());
            values.put(CHILD_CATEGORIES_COL_HIDDEN, 1);
            values.put(CHILD_CATEGORIES_COL_PARENT_ID, -1);
            values.put(CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE, category.getDefaultExpenseType());

            db.insert(TABLE_CHILD_CATEGORIES, null, values);
        }
    }
}
