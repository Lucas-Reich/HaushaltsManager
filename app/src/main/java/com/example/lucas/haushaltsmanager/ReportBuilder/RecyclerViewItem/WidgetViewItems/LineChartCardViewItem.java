package com.example.lucas.haushaltsmanager.ReportBuilder.RecyclerViewItem.WidgetViewItems;

import android.content.Context;

import com.example.lucas.haushaltsmanager.ReportBuilder.Widgets.TotalExpenseTrendLineChart;

public class LineChartCardViewItem extends CardViewItem {
    public LineChartCardViewItem(Context context) {
        super(new TotalExpenseTrendLineChart(context));
    }
}
