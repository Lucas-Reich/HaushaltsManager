package com.example.lucas.haushaltsmanager.Database.Migrations;

import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.ACCOUNTS_COL_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CHILD_CATEGORIES_COL_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CURRENCIES_COL_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_ACCOUNTS;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_CHILD_CATEGORIES;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_CURRENCIES;

public final class TemplateBookingsTable {
    public static final String TB_TABLE = "TEMPLATE_BOOKINGS";

    public static  final String TB_ID = "id";
    public static  final String TB_CREATED_AT = "created_at";
    public static  final String TB_EXPENSE_TYPE = "expense_type";
    public static  final String TB_PRICE = "price";
    public static  final String TB_CATEGORY_ID = "category_id";
    public static  final String TB_EXPENDITURE = "expenditure";
    public static  final String TB_TITLE = "title";
    public static  final String TB_DATE = "date";
    public static  final String TB_ACCOUNT_ID = "account_id";
    public static  final String TB_CURRENCY_ID = "currency_id";
}
