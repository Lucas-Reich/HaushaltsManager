package com.example.lucas.haushaltsmanager.Database.Repositories.RecurringBookings;

import android.database.Cursor;

import com.example.lucas.haushaltsmanager.Database.ExpensesDbHelper;
import com.example.lucas.haushaltsmanager.Database.TransformerInterface;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Frequency;
import com.example.lucas.haushaltsmanager.Entities.Price;
import com.example.lucas.haushaltsmanager.Entities.RecurringBooking;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_ACCOUNT_ID;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_DATE;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_EXPENDITURE;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_EXPENSE_TYPE;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_PRICE;
import static com.example.lucas.haushaltsmanager.Database.Migrations.TemplateBookingsTable.TB_TITLE;

public class RecurringBookingTransformer implements TransformerInterface<RecurringBooking> {
    private final TransformerInterface<Currency> currencyTransformer;
    private final TransformerInterface<Category> childCategoryTransformer;

    public RecurringBookingTransformer(
            TransformerInterface<Currency> currencyTransformer,
            TransformerInterface<Category> childCategoryTransformer
    ) {
        this.currencyTransformer = currencyTransformer;
        this.childCategoryTransformer = childCategoryTransformer;
    }

    @Override
    public RecurringBooking transform(Cursor c) {
        if (c.isAfterLast()) {
            return null;
        }

        return fromCursor(c);
    }

    private RecurringBooking fromCursor(Cursor c) {
        long index = c.getLong(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_ID));
        long startInMillis = c.getLong(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_OCCURRENCE));
        long endInMillis = c.getLong(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_END));

        return RecurringBooking.load(
                index,
                createFromMillis(startInMillis),
                createFromMillis(endInMillis),
                extractFrequency(c),
                extractExpense(c)
        );
    }

    private ExpenseObject extractExpense(Cursor c) {
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

    private Frequency extractFrequency(Cursor c) {
        int calendarField = c.getInt(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_CALENDAR_FIELD));
        int amount = c.getInt(c.getColumnIndex(ExpensesDbHelper.RECURRING_BOOKINGS_COL_AMOUNT));

        return new Frequency(
                calendarField,
                amount
        );
    }

    private Calendar createFromMillis(long millis) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(millis);

        return date;
    }
}
