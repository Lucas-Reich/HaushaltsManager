package com.example.lucas.haushaltsmanager.Database.Repositories.Bookings;

import android.database.Cursor;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.ChildExpenseRepository;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Price;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class BookingTransformer implements TransformerInterface<ExpenseObject> {
    private final TransformerInterface<Category> categoryTransformer;

    public BookingTransformer(
            TransformerInterface<Category> categoryTransformer
    ) {
        this.categoryTransformer = categoryTransformer;
    }

    @Override
    public ExpenseObject transform(Cursor c) {
        UUID expenseId = getId(c);
        String title = c.getString(c.getColumnIndex("title"));
        Price price = getPrice(c);
        Calendar date = getDate(c);
        String notice = c.getString(c.getColumnIndex("notice"));
        UUID accountId = getAccountId(c);
        ExpenseObject.EXPENSE_TYPES expenseType = getExpenseType(c);
        Category category = categoryTransformer.transform(c);

        if (c.isLast()) {
            c.close();
        }

        List<ExpenseObject> children = new ArrayList<>();
        if (expenseType.equals(ExpenseObject.EXPENSE_TYPES.PARENT_EXPENSE)) {
            children = new ChildExpenseRepository(app.getContext()).getAll(expenseId);
        }

        return new ExpenseObject(
                expenseId,
                title,
                price,
                date,
                category,
                notice,
                accountId,
                expenseType,
                children
        );
    }

    private UUID getId(Cursor c) {
        String rawId = c.getString(c.getColumnIndex("id"));

        return UUID.fromString(rawId);
    }

    private UUID getAccountId(Cursor c) {
        String rawAccountId = c.getString(c.getColumnIndex("account_id"));

        return UUID.fromString(rawAccountId);
    }

    private Price getPrice(Cursor c) {
        double rawPrice = c.getDouble(c.getColumnIndex("price"));
        boolean expenditure = c.getInt(c.getColumnIndex("expenditure")) == 1;

        return new Price(rawPrice, expenditure);
    }

    private Calendar getDate(Cursor c) {
        String dateString = c.getString(c.getColumnIndex("date"));

        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(Long.parseLong(dateString));

        return date;
    }

    private ExpenseObject.EXPENSE_TYPES getExpenseType(Cursor c) {
        String rawExpenseType = c.getString(c.getColumnIndex("expense_type"));

        return ExpenseObject.EXPENSE_TYPES.valueOf(rawExpenseType);
    }
}
