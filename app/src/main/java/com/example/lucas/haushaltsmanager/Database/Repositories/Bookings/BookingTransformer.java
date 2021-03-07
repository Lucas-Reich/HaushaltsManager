package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings;

import android.database.Cursor;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepositoryInterface;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BookingTransformer implements TransformerInterface<ExpenseObject> {
    private final TransformerInterface<Currency> currencyTransformer;
    private final TransformerInterface<Category> childCategoryTransformer;

    public BookingTransformer(
            TransformerInterface<Currency> currencyTransformer,
            TransformerInterface<Category> childCategoryTransformer
    ) {
        this.currencyTransformer = currencyTransformer;
        this.childCategoryTransformer = childCategoryTransformer;
    }

    @Override
    public ExpenseObject transform(Cursor c) {
        int expenseId = c.getInt(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_ID));
        Calendar date = Calendar.getInstance();
        String dateString = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_DATE));
        date.setTimeInMillis(Long.parseLong(dateString));
        String title = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_TITLE));
        double rawPrice = c.getDouble(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_PRICE));
        boolean expenditure = c.getInt(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE)) == 1;
        String notice = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_NOTICE));
        long accountId = c.getLong(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID));
        ExpenseObject.EXPENSE_TYPES expenseType = ExpenseObject.EXPENSE_TYPES.valueOf(c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_EXPENSE_TYPE)));
        Category category = childCategoryTransformer.transform(c);
        Currency currency = currencyTransformer.transform(c);

        if (c.isLast())
            c.close();

        List<ExpenseObject> children = new ArrayList<>();
        if (expenseType.equals(ExpenseObject.EXPENSE_TYPES.PARENT_EXPENSE)) {
            children = new ChildExpenseRepository(app.getContext()).getAll(expenseId);
        }

        return new ExpenseObject(
                expenseId,
                title,
                new Price(rawPrice, expenditure, currency),
                date,
                category,
                notice,
                accountId,
                expenseType,
                children,
                currency
        );
    }
}
