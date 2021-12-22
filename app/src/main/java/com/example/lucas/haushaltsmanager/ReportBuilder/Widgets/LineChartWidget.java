package com.example.lucas.haushaltsmanager.ReportBuilder.Widgets;

import android.content.Context;
import android.view.View;

import com.example.lucas.haushaltsmanager.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class LineChartWidget implements Widget {
    private final Context context;

    public LineChartWidget(Context context) {
        this.context = context;
    }

    @Override
    public View getView() {
        return initChartWithDefaultValues(context);
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_line_chart_black;
    }

    private LineChart initChartWithDefaultValues(Context context) {
        LineChart chart = new LineChart(context);

        chart.setData(new LineData(new LineDataSet(new ArrayList<Entry>() {{
            add(new Entry(0, 30));
            add(new Entry(5, 60));
            add(new Entry(10, 120));
            add(new Entry(15, 240));
            add(new Entry(20, 480));
        }}, "Meine Daten")));

        return chart;
    }
}
