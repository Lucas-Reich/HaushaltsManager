package com.example.lucas.haushaltsmanager.Entities.Report;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.example.lucas.haushaltsmanager.App.app;
import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Utils.ExpenseUtils.ExpenseSum;
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
        setCardTitle(cardTitle);
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

        return expenseSum.byExpenditureType(false, mExpenses);
    }

    @Override
    public double getOutgoing() {
        ExpenseSum expenseSum = new ExpenseSum();

        return expenseSum.byExpenditureType(true, mExpenses);
    }

    @Override
    public int getBookingCount() {
        return getExpensesCount(mExpenses);
    }

    @Override
    public Category getMostStressedCategory() {
        HashMap<Category, Double> categories = sumExpensesByCategory();

        if (categories.isEmpty())
            return getPlaceholderCategory(R.string.no_expenses);

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

    @Override
    public void setCardTitle(String title) {
        mCardTitle = title;
    }

    private String getResourceString(@StringRes int stringRes) {
        return app.getContext().getString(stringRes);
    }

    private Category getPlaceholderCategory(@StringRes int titleRes) {
        return new Category(
                getResourceString(titleRes),
                "#FFFFFF",
                false,
                new ArrayList<Category>()
        );
    }

    private HashMap<Category, Double> sumExpensesByCategory() {
        ExpenseSum expenseSum = new ExpenseSum();

        return expenseSum.byCategory(mExpenses);
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
