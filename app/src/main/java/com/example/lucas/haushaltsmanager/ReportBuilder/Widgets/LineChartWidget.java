package com.example.lucas.haushaltsmanager.ReportBuilder.Widgets;

import android.content.Context;
import android.view.View;

import com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity.ConfigurationObject;
import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class LineChartWidget implements Widget {
    private LineChart lineChart;

    public LineChartWidget(Context context) {
        this.lineChart = new LineChart(context);
    }

    @Override
    public View getView() {
        return this.lineChart;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_line_chart_black;
    }

    @Override
    public void updateView(ConfigurationObject configuration) {
        LineData data = createDataFromBookings(configuration.getBookings());

        lineChart.setData(data);

        lineChart.invalidate();
    }

    public Widget cloneWidget(Context context) {
        LineChart lineChart = new LineChart(context);
        lineChart.setData(this.lineChart.getData());

        LineChartWidget widget = new LineChartWidget(context);
        widget.lineChart = lineChart;

        return widget;
    }

    private LineData createDataFromBookings(List<Booking> bookings) {
        List<Entry> dataSet = new ArrayList<>();

        int counter = 0;
        for (Booking booking : bookings) {
            dataSet.add(new Entry(counter, (float) booking.getPrice().getAbsoluteValue()));
            counter += 5;
        }

        return new LineData(new LineDataSet(dataSet, "Meine Daten"));
    }
}
