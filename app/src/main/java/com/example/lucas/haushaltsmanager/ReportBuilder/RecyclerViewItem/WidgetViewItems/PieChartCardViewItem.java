package com.example.lucas.haushaltsmanager.ReportBuilder.RecyclerViewItem.WidgetViewItems;

import android.content.Context;

import com.example.lucas.haushaltsmanager.ReportBuilder.Widgets.IncomeExpensePieChartWidget;

public class PieChartCardViewItem extends CardViewItem {
    public PieChartCardViewItem(Context context) {
        super(new IncomeExpensePieChartWidget(context));
    }
}
