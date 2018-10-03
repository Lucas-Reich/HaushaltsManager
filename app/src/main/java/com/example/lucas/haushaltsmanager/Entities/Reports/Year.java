package com.example.lucas.haushaltsmanager.Entities.Reports;

import android.support.annotation.NonNull;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Reports.ReportInterface;

import java.util.List;

public class Year implements ReportInterface {
    private String mCardTitle;
    private List<ExpenseObject> mExpenses;
    private Currency mCurrency;

    public Year(@NonNull String cardTitle, @NonNull List<ExpenseObject> expenses, @NonNull Currency currency) {

        mCardTitle = cardTitle;
        mExpenses = expenses;
        mCurrency = currency;
    }

    @Override
    public double getTotal() {

        return getIncoming() - getOutgoing();
    }

    @Override
    public double getIncoming() {
        return 0;
    }

    @Override
    public double getOutgoing() {
        return 0;
    }

    @Override
    public int getBookingCount() {

        return mExpenses.size();
    }

    @Override
    public Category getMostStressedCategory() {
        return null;
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
