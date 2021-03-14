package com.example.lucas.haushaltsmanager.Database.Repositories.Templates;

import android.database.Cursor;

import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.Entities.Template;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_ACCOUNT_ID;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_DATE;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_EXPENDITURE;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_EXPENSE_TYPE;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_ID;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_PRICE;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_TITLE;

public class TemplateTransformer implements TransformerInterface<Template> {
    private final TransformerInterface<Currency> currencyTransformer;
    private final TransformerInterface<Category> childCategoryTransformer;

    public TemplateTransformer(
            TransformerInterface<Currency> currencyTransformer,
            TransformerInterface<Category> childCategoryTransformer
    ) {
        this.currencyTransformer = currencyTransformer;
        this.childCategoryTransformer = childCategoryTransformer;
    }

    @Override
    public Template transform(Cursor c) {
        int templateId = c.getInt(c.getColumnIndex(TB_ID));

        return new Template(
                templateId,
                transformExpense(c)
        );
    }

    private ExpenseObject transformExpense(Cursor c) {
        String title = c.getString(c.getColumnIndex(TB_TITLE));
        long accountId = c.getLong(c.getColumnIndex(TB_ACCOUNT_ID));
        Category category = childCategoryTransformer.transform(c);
        Currency currency = currencyTransformer.transform(c);

        return new ExpenseObject(
                -1,
                title,
                extractPrice(c, currency),
                extractDate(c),
                category,
                "",
                accountId,
                extractExpenseType(c),
                new ArrayList<ExpenseObject>(),
                currency
        );
    }

    private ExpenseObject.EXPENSE_TYPES extractExpenseType(Cursor c) {
        String rawExpenseType = c.getString(c.getColumnIndex(TB_EXPENSE_TYPE));

        return ExpenseObject.EXPENSE_TYPES.valueOf(rawExpenseType);
    }

    private Calendar extractDate(Cursor c) {
        Calendar date = Calendar.getInstance();
        String dateString = c.getString(c.getColumnIndex(TB_DATE));
        date.setTimeInMillis(Long.parseLong(dateString));

        return date;
    }

    private Price extractPrice(Cursor c, Currency currency) {
        double rawPrice = c.getDouble(c.getColumnIndex(TB_PRICE));
        boolean expenditure = c.getInt(c.getColumnIndex(TB_EXPENDITURE)) == 1;

        return new Price(rawPrice, expenditure, currency);

    }
}
