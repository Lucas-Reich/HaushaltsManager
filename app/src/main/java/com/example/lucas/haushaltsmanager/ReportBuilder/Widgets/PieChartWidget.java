package com.example.lucas.haushaltsmanager.ReportBuilder.Widgets;

import android.content.Context;
import android.view.View;

import com.example.lucas.haushaltsmanager.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class PieChartWidget implements Widget {
    private final Context context;

    public PieChartWidget(Context context) {
        this.context = context;
    }

    @Override
    public View getView() {
        return initChartWithDefaultValues(context);
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_pie_chart;
    }

    private PieChart initChartWithDefaultValues(Context context) {
        PieChart chart = new PieChart(context);

        chart.setData(new PieData(new PieDataSet(
                new ArrayList<PieEntry>() {{
                    add(new PieEntry(100f, "Eintrag"));
                }}, "Test"
        )));

        return chart;
    }
}
