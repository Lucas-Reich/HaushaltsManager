package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

import android.database.Cursor;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildCategories.ChildCategoryRepository;
import com.example.lucas.haushaltsmanager.Database.Repositories.Currencies.CurrencyTransformer;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;

import java.util.ArrayList;
import java.util.Calendar;

public class ChildExpenseTransformer implements TransformerInterface<ExpenseObject> {
    private final CurrencyTransformer currencyTransformer;

    public ChildExpenseTransformer(CurrencyTransformer currencyTransformer) {
        this.currencyTransformer = currencyTransformer;
    }

    @Override
    public ExpenseObject transform(Cursor c) {
        long expenseId = c.getLong(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_ID));
        Calendar date = Calendar.getInstance();
        String dateString = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_DATE));
        date.setTimeInMillis(Long.parseLong(dateString));
        String title = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_TITLE));
        double price = c.getDouble(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_PRICE));
        boolean expenditure = c.getInt(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_EXPENDITURE)) == 1;
        String notice = c.getString(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_NOTICE));
        long accountId = c.getLong(c.getColumnIndex(ExpensesDbHelper.BOOKINGS_COL_ACCOUNT_ID));
        Category expenseCategory = ChildCategoryRepository.cursorToChildCategory(c);
        Currency expenseCurrency = currencyTransformer.transform(c);

        if (c.isLast())
            c.close();

        return new ExpenseObject(
            expenseId,
            title,
            new Price(price, expenditure, expenseCurrency),
            date,
            expenseCategory,
            notice,
            accountId,
            ExpenseObject.EXPENSE_TYPES.CHILD_EXPENSE,
            new ArrayList<ExpenseObject>(),
            expenseCurrency
        );
    }
}
