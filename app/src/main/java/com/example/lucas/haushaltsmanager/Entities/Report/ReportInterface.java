package com.example.lucas.haushaltsmanager.Entities.Report;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Booking.IBooking;

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
