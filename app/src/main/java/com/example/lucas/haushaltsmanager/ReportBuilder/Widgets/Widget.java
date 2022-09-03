package com.example.lucas.haushaltsmanager.ReportBuilder.Widgets;

import android.content.Context;
import android.view.View;

import com.example.lucas.haushaltsmanager.entities.booking.Booking;

import java.util.List;

public interface Widget {
    View getView();

    int getIcon();

    void updateView(List<Booking> bookings);

    Widget cloneWidget(Context context);
}
