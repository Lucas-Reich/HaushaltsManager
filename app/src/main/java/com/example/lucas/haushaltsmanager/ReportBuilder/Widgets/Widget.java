package com.example.lucas.haushaltsmanager.ReportBuilder.Widgets;

import android.view.View;

import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

import java.util.List;

public interface Widget {
    boolean equals(Object other);

    View getView();

    void setData(List<ExpenseObject> expenses);

    int getIcon();
}
