package com.example.lucas.haushaltsmanager.ReportBuilder.Widgets;

import android.content.Context;
import android.view.View;

import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class LineChartWidget implements Widget {
    private LineChart chart;

    public LineChartWidget(Context context) {
        initChartWithDefaultValues(context);
    }

    @Override
    public View getWidgetView() {
        return this.chart;
    }

    @Override
    public void setData(List<ExpenseObject> expenses) {
        chart.invalidate();
    }

    private void initChartWithDefaultValues(Context context) {
        this.chart = new LineChart(context);

        this.chart.setData(new LineData(new LineDataSet(new ArrayList<Entry>() {{
            add(new Entry(0, 30));
            add(new Entry(5, 60));
            add(new Entry(10, 120));
            add(new Entry(15, 240));
            add(new Entry(20, 480));
        }}, "Meine Daten")));
    }
}
