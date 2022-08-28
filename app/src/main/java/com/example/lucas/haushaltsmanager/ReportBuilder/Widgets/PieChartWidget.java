package com.example.lucas.haushaltsmanager.ReportBuilder.Widgets;

import android.content.Context;
import android.view.View;

import com.example.lucas.haushaltsmanager.Activities.DragAndDropActivity.ConfigurationObject;
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
        this.pieChart = initChartWithDefaultValues(context);
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
    public void updateView(ConfigurationObject configuration) {
        PieData pieData = createDataFromBookings(configuration.getBookings());

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
