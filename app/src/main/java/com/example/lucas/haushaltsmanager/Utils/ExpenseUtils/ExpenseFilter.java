package com.example.lucas.haushaltsmanager.Utils.ExpenseUtils;

import com.example.lucas.haushaltsmanager.entities.booking.Booking;

import java.util.ArrayList;
import java.util.List;

public class ExpenseFilter {
    public List<Booking> byExpenditureType(List<Booking> expenses, boolean filter) {
        List<Booking> filteredExpenses = new ArrayList<>();

        for (Booking expense : expenses) {
            if (expense.getPrice().isNegative() != filter) {
                continue;
            }

            filteredExpenses.add(expense);
        }

        return filteredExpenses;
    }
}
