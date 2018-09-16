package com.example.lucas.haushaltsmanager;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Currency;

public interface IPieChartCardView {
    //hier sollen methode rein die für MonthlyReport, YearlyReport, ... benutzt werden
    double getTotal();

    double getIncoming();

    double getOutgoing();

    int getBookingCount();

    Category getMostStressedCategory();

    String getCardTitle();

    Currency getCurrency();
}
