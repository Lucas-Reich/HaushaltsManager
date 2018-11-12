package com.example.lucas.haushaltsmanager.Entities.Report;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.ExpenseSum;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Report implements ReportInterface {
    private String mCardTitle;
    private List<ExpenseObject> mExpenses;
    private Currency mCurrency;

    public Report(
            @NonNull String cardTitle,
            @NonNull List<ExpenseObject> expenses,
            @NonNull Currency currency
    ) {
        mCardTitle = cardTitle;
        mExpenses = expenses;
        mCurrency = currency;
    }

    @NonNull
    public List<ExpenseObject> getExpenses() {
        return mExpenses;
    }

    @Override
    public double getTotal() {

        return getIncoming() + getOutgoing();
    }

    @Override
    public double getIncoming() {
        ExpenseSum expenseSum = new ExpenseSum();

        return expenseSum.sumBookingsByExpenditureType(false, mExpenses);
    }

    @Override
    public double getOutgoing() {
        ExpenseSum expenseSum = new ExpenseSum();

        return expenseSum.sumBookingsByExpenditureType(true, mExpenses);
    }

    @Override
    public int getBookingCount() {
        return getExpensesCount(mExpenses);
    }

    @Override
    public Category getMostStressedCategory(Context context) {
        HashMap<Category, Double> categories = sumExpensesByCategory();

        if (categories.isEmpty())
            return getPlaceholderCategory(R.string.no_expenses, context);

        return getMaxEntry(categories).getKey();
    }

    @Override
    public String getCardTitle() {
        return mCardTitle;
    }

    @Override
    public Currency getCurrency() {
        return mCurrency;
    }

    private String getResourceString(@StringRes int stringRes, Context context) {
        return context.getResources().getString(stringRes);
    }

    private Category getPlaceholderCategory(@StringRes int titleRes, Context context) {
        return new Category(
                getResourceString(titleRes, context),
                "#FFFFFF",
                false,
                new ArrayList<Category>()
        );
    }

    private HashMap<Category, Double> sumExpensesByCategory() {
        ExpenseSum expenseSum = new ExpenseSum();

        return expenseSum.sumBookingsByCategory(mExpenses);
    }

    private Map.Entry<Category, Double> getMaxEntry(HashMap<Category, Double> categoryDoubleHashMap) {
        Map.Entry<Category, Double> minCategory = null;

        for (Map.Entry<Category, Double> entry : categoryDoubleHashMap.entrySet()) {
            if (null == minCategory || entry.getValue() < minCategory.getValue()) {
                minCategory = entry;
            }
        }

        return minCategory;
    }

    private int getExpensesCount(List<ExpenseObject> expenses) {
        int count = 0;

        for (ExpenseObject expense : expenses) {
            if (expense.isParent())
                count += expense.getChildren().size();
            else
                count += 1;
        }

        return count;
    }
}
