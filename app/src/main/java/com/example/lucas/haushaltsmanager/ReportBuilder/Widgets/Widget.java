package com.example.lucas.haushaltsmanager.ReportBuilder.Widgets;

import android.view.View;

import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

import java.util.List;

public interface Widget {
    View getWidgetView();

    void setData(List<ExpenseObject> expenses);
}
