package com.example.lucas.haushaltsmanager.Utils.ExpenseUtils;

import com.example.lucas.haushaltsmanager.Entities.Expense.Booking;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ExpenseSorter {
    public static final String SORT_DESC = "DESC";
    public static final String SORT_ASC = "ASC";

    public void byDate(List<ExpenseObject> bookings, final String order) {
        Collections.sort(bookings, new Comparator<Booking>() {
            @Override
            public int compare(Booking booking1, Booking booking2) {
                if (order.equals(SORT_DESC)) {
                    return booking1.getDate().compareTo(booking2.getDate());
                }

                return booking2.getDate().compareTo(booking1.getDate());
            }
        });
    }
}
