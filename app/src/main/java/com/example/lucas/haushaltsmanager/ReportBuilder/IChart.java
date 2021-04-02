package com.example.lucas.haushaltsmanager.ReportBuilder;

import android.view.View;

import com.example.lucas.haushaltsmanager.RecyclerView.Items.Booking.BookingItem.ExpenseItem;

import java.util.List;

public interface IChart {
    void setData(List<ExpenseItem> expenses);

    Object getConfigurableParameters();

    int getImage();

    View getChart();
}
