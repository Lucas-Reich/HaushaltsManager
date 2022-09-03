package com.example.lucas.haushaltsmanager.ReportBuilder.Widgets;

import android.content.Context;
import android.view.View;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class PieChartWidget implements Widget {
    private PieChart pieChart;

    public PieChartWidget(Context context) {
        this.pieChart = new PieChart(context);
    }

    @Override
    public View getView() {
        return this.pieChart;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_pie_chart;
    }

    @Override
    public void updateView(List<Booking> bookings) {
        PieData pieData = createDataFromBookings(bookings);

        this.pieChart.setData(pieData);

        pieChart.invalidate();
    }

    public Widget cloneWidget(Context context) {
        PieChart pieChart = new PieChart(context);
        pieChart.setData(this.pieChart.getData());

        PieChartWidget widget = new PieChartWidget(context);
        widget.pieChart = pieChart;

        return widget;
    }

    private PieData createDataFromBookings(List<Booking> bookings) {
        List<PieEntry> dataSet = new ArrayList<>();
        for (Booking booking : bookings) {
            dataSet.add(new PieEntry((float) booking.getPrice().getAbsoluteValue(), booking.getTitle()));
        }

        return new PieData(new PieDataSet(dataSet, "Meine Daten"));
    }
}
