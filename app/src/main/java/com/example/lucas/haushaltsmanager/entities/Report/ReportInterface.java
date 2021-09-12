package com.example.lucas.haushaltsmanager.entities.Report;

import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.entities.Booking.IBooking;

import java.util.List;

public interface ReportInterface {
    double getTotal();

    double getIncoming();

    double getOutgoing();

    int getBookingCount();

    Category getMostStressedCategory();

    String getCardTitle();

    void setCardTitle(String title);

    List<IBooking> getExpenses();
}
