package com.example.lucas.haushaltsmanager.Entities.Report;

import android.content.Context;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

import java.util.List;

public interface ReportInterface {
    //hier sollen methode rein die für MonthlyReport, YearlyReport, ... benutzt werden
    double getTotal();

    double getIncoming();

    double getOutgoing();

    int getBookingCount();

    Category getMostStressedCategory(Context context);

    String getCardTitle();

    void setCardTitle(String title);

    Currency getCurrency();

    List<ExpenseObject> getExpenses();
}
