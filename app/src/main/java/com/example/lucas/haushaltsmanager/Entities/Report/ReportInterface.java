package com.example.lucas.haushaltsmanager.Entities.Report;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

import java.util.List;

public interface ReportInterface {
    double getTotal();

    double getIncoming();

    double getOutgoing();

    int getBookingCount();

    Category getMostStressedCategory();

    String getCardTitle();

    void setCardTitle(String title);

    Currency getCurrency();

    List<ExpenseObject> getExpenses();
}
