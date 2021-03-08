package com.example.lucas.haushaltsmanager.Database.Migrations;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Color;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseType;
import com.example.lucas.haushaltsmanager.PreferencesHelper.UserSettingsPreferences;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.ACCOUNTS_COL_BALANCE;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.ACCOUNTS_COL_CURRENCY_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.ACCOUNTS_COL_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.ACCOUNTS_COL_NAME;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_CREATED_AT;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_DATE;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_HIDDEN;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_NOTICE;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_PARENT_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_PRICE;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_TITLE;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_TAGS_COL_BOOKING_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_TAGS_COL_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_TAGS_COL_TAG_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CATEGORIES_COL_COLOR;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CATEGORIES_COL_DEFAULT_EXPENSE_TYPE;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CATEGORIES_COL_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CATEGORIES_COL_NAME;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CHILD_CATEGORIES_COL_HIDDEN;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CHILD_CATEGORIES_COL_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CHILD_CATEGORIES_COL_PARENT_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CURRENCIES_COL_CREATED_AT;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CURRENCIES_COL_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CURRENCIES_COL_NAME;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CURRENCIES_COL_SYMBOL;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.RECURRING_BOOKINGS_COL_BOOKING_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.RECURRING_BOOKINGS_COL_END;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.RECURRING_BOOKINGS_COL_FREQUENCY;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.RECURRING_BOOKINGS_COL_OCCURRENCE;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_ACCOUNTS;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_BOOKINGS;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_BOOKINGS_TAGS;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_CATEGORIES;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_CHILD_CATEGORIES;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_CURRENCIES;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_RECURRING_BOOKINGS;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_TAGS;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_TEMPLATE_BOOKINGS;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TAGS_COL_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TAGS_COL_NAME;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TEMPLATE_COL_BOOKING_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TEMPLATE_COL_ID;

final class InitialDatabaseCreation implements IMigration {
    private static final String TAG = InitialDatabaseCreation.class.getSimpleName();

    private static final String CREATE_CURRENCIES = "CREATE TABLE " + TABLE_CURRENCIES
            + "("
            + CURRENCIES_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CURRENCIES_COL_CREATED_AT + " DEFAULT CURRENT_TIMESTAMP, "
            + CURRENCIES_COL_SYMBOL + " TEXT, "
            + CURRENCIES_COL_NAME + " TEXT NOT NULL, "
            + CURRENCIES_COL_SHORT_NAME + " TEXT NOT NULL"
            + ");";

    @Deprecated
    private static final String CREATE_BOOKINGS_TAGS = "CREATE TABLE " + TABLE_BOOKINGS_TAGS
            + "("
            + BOOKINGS_TAGS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + BOOKINGS_TAGS_COL_TAG_ID + " INTEGER NOT NULL, "
            + BOOKINGS_TAGS_COL_BOOKING_ID + " INTEGER NOT NULL "
            + ");";

    private final static String CREATE_CHILD_CATEGORIES = "CREATE TABLE " + TABLE_CHILD_CATEGORIES
            + "("
            + CHILD_CATEGORIES_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CHILD_CATEGORIES_COL_NAME + " TEXT NOT NULL, "
            + CHILD_CATEGORIES_COL_COLOR + " TEXT NOT NULL, "
            + CHILD_CATEGORIES_COL_HIDDEN + " INTEGER NOT NULL, "
            + CHILD_CATEGORIES_COL_PARENT_ID + " INTEGER NOT NULL, "
            + CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE + " INTEGER NOT NULL"
            + ");";

    private final static String CREATE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES
            + "("
            + CATEGORIES_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CATEGORIES_COL_NAME + " TEXT NOT NULL, "
            + CATEGORIES_COL_COLOR + " TEXT NOT NULL, "
            + CATEGORIES_COL_DEFAULT_EXPENSE_TYPE + " INTEGER NOT NULL"
            + ");";

    @Deprecated
    private static final String CREATE_TAGS = "CREATE TABLE " + TABLE_TAGS
            + "("
            + TAGS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TAGS_COL_NAME + " TEXT NOT NULL"
            + ");";

    private static final String CREATE_ACCOUNTS = "CREATE TABLE " + TABLE_ACCOUNTS
            + "("
            + ACCOUNTS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ACCOUNTS_COL_NAME + " TEXT NOT NULL, "
            + ACCOUNTS_COL_BALANCE + " INTEGER, "
            + ACCOUNTS_COL_CURRENCY_ID + " INTEGER NOT NULL"
            + ");";

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
            + BOOKINGS_COL_CURRENCY_ID + " INTEGER NOT NULL, "
            + BOOKINGS_COL_HIDDEN + " INTEGER NOT NULL, "
            + BOOKINGS_COL_PARENT_ID + " INTEGER "
            + ");";

    private static final String CREATE_RECURRING_BOOKINGS = "CREATE TABLE " + TABLE_RECURRING_BOOKINGS
            + "("
            + RECURRING_BOOKINGS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RECURRING_BOOKINGS_COL_BOOKING_ID + " INTEGER NOT NULL, "
            + RECURRING_BOOKINGS_COL_OCCURRENCE + " TEXT NOT NULL, "
            + RECURRING_BOOKINGS_COL_FREQUENCY + " INTEGER NOT NULL, "
            + RECURRING_BOOKINGS_COL_END + " TEXT NOT NULL"
            + ");";

    private static final String CREATE_TEMPLATE_BOOKINGS = "CREATE TABLE " + TABLE_TEMPLATE_BOOKINGS
            + "("
            + TEMPLATE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TEMPLATE_COL_BOOKING_ID + " INTEGER NOT NULL "
            + ");";

    public void apply(SQLiteDatabase db) {

        try {

            Log.d(TAG, "Die Tabelle Bookings wird mit SQL-Befehl: " + CREATE_BOOKINGS + " angelegt.");
            db.execSQL(CREATE_BOOKINGS);

            Log.d(TAG, "Die Tabelle Accounts wird mit SQL-Befehl: " + CREATE_ACCOUNTS + " angelegt.");
            db.execSQL(CREATE_ACCOUNTS);

            Log.d(TAG, "Die Tabelle Tags wird mit SQL-Befehl: " + CREATE_TAGS + " angelegt.");
            db.execSQL(CREATE_TAGS);

            Log.d(TAG, "Die Tabelle Categories wird mit dem SQL-Befehl: " + CREATE_CATEGORIES + " angelegt.");
            db.execSQL(CREATE_CATEGORIES);

            Log.d(TAG, "Die Tabelle ChildCategories wird mit SQL-Befehl: " + CREATE_CHILD_CATEGORIES + " angelegt.");
            db.execSQL(CREATE_CHILD_CATEGORIES);

            Log.d(TAG, "Die Tabelle Bookings_Tags wird mit SQL-Befehl: " + CREATE_BOOKINGS_TAGS + " angelegt.");
            db.execSQL(CREATE_BOOKINGS_TAGS);

            Log.d(TAG, "Die Tabelle Template_Bookings wird mit SQL-Befehl: " + CREATE_TEMPLATE_BOOKINGS + " angelegt.");
            db.execSQL(CREATE_TEMPLATE_BOOKINGS);

            Log.d(TAG, "Die Tabelle Recurring_Bookings wird mit SQL-Befehl: " + CREATE_RECURRING_BOOKINGS + " angelegt.");
            db.execSQL(CREATE_RECURRING_BOOKINGS);

            Log.d(TAG, "Die Tabelle Currencies wird mit SQL-Befehl: " + CREATE_CURRENCIES + " angelegt.");
            db.execSQL(CREATE_CURRENCIES);

            Log.d(TAG, "Creating default Categories");
            insertHiddenCategories(db);

            Log.d(TAG, "Creating default Currencies");
            insertCurrencies(db);

        } catch (Exception ex) {

            Log.e(TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }

    public void revert(SQLiteDatabase db) {
        // TODO: Was soll ich machen
    }

    /**
     * initialization of currency table
     *
     * @param db reference to editable mDatabase
     */
    private static void insertCurrencies(SQLiteDatabase db) {
        Log.d(TAG, "Inserting default currencies");
        UserSettingsPreferences preferences = new UserSettingsPreferences(app.getContext());

        //details from: https://developers.google.com/public-data/docs/canonical/currencies_csv
        // IMPROVEMENT: Die Währungen sollten aus einer XML Datei ausgelesen werden
        List<Currency> currencies = new ArrayList<>();
        currencies.add(new Currency("Australian Dollar", "AUD", "$"));
        currencies.add(new Currency("Bulgarian Lev", "BGN", "лв"));
        currencies.add(new Currency("Brazilian Real", "BRL", "R$"));
        currencies.add(new Currency("Canadian Dollar", "CAD", "$"));
        currencies.add(new Currency("Swiss Franc", "CHF", "CHF"));
        currencies.add(new Currency("Yuan Reminbi", "CNY", "¥"));
        currencies.add(new Currency("Czech Koruna", "CZK", "Kč"));
        currencies.add(new Currency("Danish Krone", "DKK", "kr"));
        currencies.add(new Currency("Pound Sterling", "GBP", "£"));
        currencies.add(new Currency("Hong Kong Dollar", "HKD", "$"));
        currencies.add(new Currency("Croatian Kuna", "HRK", "kn"));
        currencies.add(new Currency("Forint", "HUF", "Ft"));
        currencies.add(new Currency("Rupiah", "IDR", "Rp"));
        currencies.add(new Currency("New Israeli Sheqel", "ILS", "₪"));
        currencies.add(new Currency("Indian Rupee", "INR", "₪"));
        currencies.add(new Currency("Yen", "JPY", "¥"));
        currencies.add(new Currency("Won", "KRW", "₩"));
        currencies.add(new Currency("Mexican Peso", "MXN", "$"));
        currencies.add(new Currency("Malaysian Ringgit", "MYR", "RM"));
        currencies.add(new Currency("Norwegian Krone", "NOK", "kr"));
        currencies.add(new Currency("New Zealand Dollar", "NZD", "$"));
        currencies.add(new Currency("Philippine Peso", "PHP", "Php"));
        currencies.add(new Currency("Zloty", "PLN", "zł"));
        currencies.add(new Currency("Romanian Leu", "RON", "lei"));
        currencies.add(new Currency("Russian Ruble", "RUB", "руб"));
        currencies.add(new Currency("Swedish Krona", "SEK", "kr"));
        currencies.add(new Currency("Singapore Dollar", "SGD", "$"));
        currencies.add(new Currency("Baht", "THB", "฿"));
        currencies.add(new Currency("Turkish Lira", "TRY", "TL"));
        currencies.add(new Currency("US Dollar", "USD", "$"));
        currencies.add(new Currency("Rand", "ZAR", "R"));
        currencies.add(new Currency("Euro", "EUR", "€"));


        for (Currency currency : currencies) {

            ContentValues values = new ContentValues();
            values.put(CURRENCIES_COL_NAME, currency.getName());
            values.put(CURRENCIES_COL_SHORT_NAME, currency.getShortName());
            values.put(CURRENCIES_COL_SYMBOL, currency.getSymbol());

            // IMPROVEMENT: Das wählen der Hauptwährung sollte auf dem Standort des Users passieren.
            //android.icu.util.Currency currency = android.icu.util.Currency.getInstance(getResources().getConfiguration().locale);
            //Quelle: https://stackoverflow.com/questions/27228514/android-is-it-possible-to-get-the-currency-code-of-the-country-where-the-user-a
            // TODO: Wenn die Standartwährung nicht gesetzt werden kann, soll der User darauf hingewiesen werden und gefragt werden dies zu tun
            long index = db.insert(TABLE_CURRENCIES, null, values);
            if (currency.getShortName().equals("EUR") && index != -1) {
                preferences.setMainCurrency(new Currency(
                        index,
                        currency.getName(),
                        currency.getShortName(),
                        currency.getSymbol()
                ));
            }
        }

        Log.d(TAG, "Finished inserting categories");
    }

    /**
     * Initialisiere System Kategorien.
     *
     * @param db Datenbank
     */
    private static void insertHiddenCategories(SQLiteDatabase db) {
        Log.d(TAG, "Started inserting hidden categories");
        // IMPROVEMENT: SystemKategorien sollten in einer XML Datei gespeichert sein, dann lassen sie sich auch einfacher übersetzen.
        Color color = new Color(app.getContext().getResources().getColor(R.color.transfer_booking_color));

        ArrayList<Category> categories = new ArrayList<>();
        categories.add(new Category(
                app.getContext().getString(R.string.category_transfer),
                color,
                ExpenseType.expense(),
                new ArrayList<Category>()
        ));


        for (Category category : categories) {

            ContentValues values = new ContentValues();
            values.put(CHILD_CATEGORIES_COL_NAME, category.getTitle());
            values.put(CHILD_CATEGORIES_COL_COLOR, category.getColor().getColorString());
            values.put(CHILD_CATEGORIES_COL_HIDDEN, 1);
            values.put(CHILD_CATEGORIES_COL_PARENT_ID, -1);
            values.put(CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE, category.getDefaultExpenseType().value());

            db.insert(TABLE_CHILD_CATEGORIES, null, values);
        }

        Log.d(TAG, "Finished inserting hidden categories");
    }
}
