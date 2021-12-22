package com.example.lucas.haushaltsmanager.ReportBuilder.RecyclerViewItem.WidgetViewItems;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.ReportBuilder.Widgets.LineChartWidget;

public class LineChartCardViewItem extends CardViewItem {
    public LineChartCardViewItem(Context context) {
        super(new LineChartWidget(context));
    }
}
