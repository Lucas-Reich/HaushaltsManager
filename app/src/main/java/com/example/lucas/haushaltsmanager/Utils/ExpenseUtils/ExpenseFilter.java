package com.example.lucas.haushaltsmanager.Utils.ExpenseUtils;

import com.example.lucas.haushaltsmanager.entities.booking.Booking;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public List<Booking> byAccount(List<Booking> bookings, List<UUID> accounts) {
        List<Booking> filteredExpenses = new ArrayList<>();

        for (Booking booking : bookings) {
            if (!hasAccount((Booking) booking, accounts)) {
                continue;
            }

            filteredExpenses.add(booking);
        }

        return filteredExpenses;
    }

    private boolean hasAccount(Booking expense, List<UUID> accounts) {
        return accounts.contains(expense.getAccountId());
    }
}
