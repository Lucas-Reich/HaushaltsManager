package com.example.lucas.haushaltsmanager.ReportBuilder.RecyclerViewItem.WidgetViewItems;

import android.content.Context;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.ReportBuilder.Widgets.PieChartWidget;

public class PieChartCardViewItem extends CardViewItem {
    public PieChartCardViewItem(Context context) {
        super(new CardViewContent(
                R.drawable.ic_pie_chart,
                new PieChartWidget(context))
        );
    }
}
