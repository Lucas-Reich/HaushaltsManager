package com.example.lucas.haushaltsmanager.Utils.ExpenseUtils;

import com.example.lucas.haushaltsmanager.entities.booking.IBooking;

import java.util.List;

public class ExpenseSorter {
    public static final String SORT_DESC = "DESC";

    public void byDate(List<IBooking> bookings, final String order) {
        bookings.sort((booking1, booking2) -> {
            if (order.equals(SORT_DESC)) {
                return booking1.getDate().compareTo(booking2.getDate());
            }

            return booking2.getDate().compareTo(booking1.getDate());
        });
    }
}
