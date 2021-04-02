package com.example.lucas.haushaltsmanager.ReportBuilder;

import android.content.Context;
import android.view.View;

import com.example.lucas.haushaltsmanager.R;
import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.BookingItem.ExpenseItem;
import com.github.mikephil.charting.data.PieData;

import java.util.List;

public class PieChart implements IChart {
    private final com.github.mikephil.charting.charts.PieChart pieChart;

    public PieChart(Context context) {
        pieChart = new com.github.mikephil.charting.charts.PieChart(context);
    }

    public void setData(List<ExpenseItem> expenses) {
        pieChart.setData(getPieData());
    }

    public Object getConfigurableParameters() {
        return null;
    }

    public int getImage() {
        return R.drawable.ic_wallet_dark;
    }

    public View getChart() {
        return pieChart;
    }

    private PieData getPieData() {
        // TODO: Transform expenses into pie data slices

        return new PieData();
    }
}
