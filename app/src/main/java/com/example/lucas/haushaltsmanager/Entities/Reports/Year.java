package com.example.lucas.haushaltsmanager.Entities.Reports;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Year implements ReportInterface {
    private String mCardTitle;
    private List<ExpenseObject> mExpenses;
    private Currency mCurrency;

    public Year(
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

        return getIncoming() - getOutgoing();
    }

    @Override
    public double getIncoming() {
        double incomingMoney = 0;
        for (ExpenseObject expense : mExpenses) {

            if (!expense.isExpenditure())
                incomingMoney += expense.getUnsignedPrice();
        }

        return incomingMoney;
    }

    @Override
    public double getOutgoing() {
        double outgoingMoney = 0;
        for (ExpenseObject expense : mExpenses) {

            if (expense.isExpenditure())
                outgoingMoney += expense.getUnsignedPrice();
        }

        return outgoingMoney;
    }

    @Override
    public int getBookingCount() {
        return mExpenses.size();
    }

    @Override
    public Category getMostStressedCategory(Context context) {
        HashMap<Category, Double> categories = sumExpensesByCategory();

        if (categories.isEmpty())
            return new Category(context.getResources().getString(R.string.no_expenses), "#FFFFFF", false, new ArrayList<Category>());

        Map.Entry<Category, Double> minCategory = null;
        for (Map.Entry<Category, Double> entry : categories.entrySet()) {
            if (null == minCategory || entry.getValue() < minCategory.getValue()) {
                minCategory = entry;
            }
        }

        return minCategory.getKey();
    }

    private HashMap<Category, Double> sumExpensesByCategory() {

        HashMap<Category, Double> categories = new HashMap<>();
        for (ExpenseObject expense : mExpenses) {
            Category expenseCategory = expense.getCategory();

            if (!categories.containsKey(expenseCategory))
                categories.put(expenseCategory, 0d);

            categories.put(expenseCategory, categories.get(expenseCategory) + expense.getSignedPrice());
        }

        return categories;
    }

    @Override
    public String getCardTitle() {
        return mCardTitle;
    }

    @Override
    public Currency getCurrency() {
        return mCurrency;
    }
}
