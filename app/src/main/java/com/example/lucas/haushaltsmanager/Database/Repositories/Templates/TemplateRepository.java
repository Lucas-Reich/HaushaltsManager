package com.example.lucas.haushaltsmanager.Database.Repositories.Templates;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.haushaltsmanager.Database.DatabaseManager;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable;
import com.example.lucas.haushaltsmanager.Database.QueryInterface;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryTransformer;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.CurrencyTransformer;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Template;

import java.util.ArrayList;
import java.util.List;

public class TemplateRepository {
    private SQLiteDatabase mDatabase;
    private final TransformerInterface<Template> transformer;

    public TemplateRepository(Context context) {
        DatabaseManager.initializeInstance(new ExpensesDbHelper(context));

        mDatabase = DatabaseManager.getInstance().openDatabase();
        transformer = new TemplateTransformer(
                new CurrencyTransformer(),
                new ChildCategoryTransformer()
        );
    }

    public List<Template> getAll() {
        Cursor c = executeRaw(new GetAllTemplateBookingsQuery());
        c.moveToFirst();

        ArrayList<Template> templateBookings = new ArrayList<>();
        while (!c.isAfterLast()) {
            templateBookings.add(transformer.transform(c));
            c.moveToNext();
        }

        c.close();
        return templateBookings;
    }

    public Template insert(Template template) {
        ExpenseObject expense = template.getTemplate();

        ContentValues values = new ContentValues();
        values.put(TemplateBookingsTable.TB_EXPENSE_TYPE, expense.getExpenseType().name());
        values.put(TemplateBookingsTable.TB_PRICE, expense.getUnsignedPrice());
        values.put(TemplateBookingsTable.TB_CATEGORY_ID, expense.getCategory().getIndex());
        values.put(TemplateBookingsTable.TB_EXPENDITURE, expense.isExpenditure());
        values.put(TemplateBookingsTable.TB_TITLE, expense.getTitle());
        values.put(TemplateBookingsTable.TB_DATE, expense.getDate().getTimeInMillis());
        values.put(TemplateBookingsTable.TB_ACCOUNT_ID, expense.getAccountId());
        values.put(TemplateBookingsTable.TB_CURRENCY_ID, expense.getCurrency().getIndex());

        long insertedTemplateId = mDatabase.insert(
                TemplateBookingsTable.TB_TABLE,
                null,
                values
        );

        return new Template(
                insertedTemplateId,
                expense
        );
    }

    private Cursor executeRaw(QueryInterface query) {
        return mDatabase.rawQuery(String.format(
                query.sql(),
                query.values()
        ), null);
    }
}
