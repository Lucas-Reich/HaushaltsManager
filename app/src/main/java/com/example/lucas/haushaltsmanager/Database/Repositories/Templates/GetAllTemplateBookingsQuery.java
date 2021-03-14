package com.example.lucas.haushaltsmanager.Database.Repositories.Templates;

import com.example.lucas.haushaltsmanager.Database.QueryInterface;

import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_CATEGORY_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.BOOKINGS_COL_CURRENCY_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CHILD_CATEGORIES_COL_COLOR;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CHILD_CATEGORIES_COL_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CHILD_CATEGORIES_COL_NAME;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CURRENCIES_COL_ID;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CURRENCIES_COL_NAME;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CURRENCIES_COL_SHORT_NAME;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.CURRENCIES_COL_SYMBOL;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_CHILD_CATEGORIES;
import static com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper.TABLE_CURRENCIES;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_ACCOUNT_ID;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_DATE;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_EXPENDITURE;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_EXPENSE_TYPE;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_ID;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_PRICE;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_TABLE;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_TITLE;

class GetAllTemplateBookingsQuery implements QueryInterface {
    @Override
    public String sql() {
        return "SELECT "
                + TB_TABLE + "." + TB_ID + ", "
                + TB_TABLE + "." + TB_EXPENSE_TYPE + ", "
                + TB_TABLE + "." + TB_PRICE + ", "
                + TB_TABLE + "." + TB_EXPENDITURE + ", "
                + TB_TABLE + "." + TB_TITLE + ", "
                + TB_TABLE + "." + TB_DATE + ", "
                + TB_TABLE + "." + TB_ACCOUNT_ID + ", "
                + TABLE_CURRENCIES + "." + CURRENCIES_COL_ID + ", "
                + TABLE_CURRENCIES + "." + CURRENCIES_COL_NAME + ", "
                + TABLE_CURRENCIES + "." + CURRENCIES_COL_SHORT_NAME + ", "
                + TABLE_CURRENCIES + "." + CURRENCIES_COL_SYMBOL + ", "
                + TABLE_CHILD_CATEGORIES + "." + CHILD_CATEGORIES_COL_ID + ", "
                + TABLE_CHILD_CATEGORIES + "." + CHILD_CATEGORIES_COL_NAME + ", "
                + TABLE_CHILD_CATEGORIES + "." + CHILD_CATEGORIES_COL_COLOR + ", "
                + TABLE_CHILD_CATEGORIES + "." + CHILD_CATEGORIES_COL_DEFAULT_EXPENSE_TYPE
                + " FROM " + TB_TABLE
                + " LEFT JOIN " + TABLE_CHILD_CATEGORIES + " ON " + TB_TABLE + "." + BOOKINGS_COL_CATEGORY_ID + " = " + TABLE_CHILD_CATEGORIES + "." + CHILD_CATEGORIES_COL_ID
                + " LEFT JOIN " + TABLE_CURRENCIES + " ON " + TB_TABLE + "." + BOOKINGS_COL_CURRENCY_ID + " = " + TABLE_CURRENCIES + "." + CURRENCIES_COL_ID
                + " ORDER BY " + TB_TABLE + "." + TB_DATE + " DESC;";
    }

    @Override
    public Object[] values() {
        return new Object[]{};
    }
}
