package com.example.lucas.haushaltsmanager.Utils.ExpenseUtils;

import com.example.lucas.haushaltsmanager.entities.Category;
import com.example.lucas.haushaltsmanager.entities.booking.Booking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseSum {
    public HashMap<Category, Double> byCategory(List<Booking> expenses) {
        HashMap<Category, List<Booking>> expensesGroupedByCategory = groupByCategory(expenses);

        HashMap<Category, Double> summedExpenses = new HashMap<>();
        for (Map.Entry<Category, List<Booking>> entry : expensesGroupedByCategory.entrySet()) {
            summedExpenses.put(
                    entry.getKey(),
                    sum(entry.getValue())
            );
        }

        return summedExpenses;
    }

    public double byExpenditureType(boolean expenditureType, List<Booking> expenses) {
        List<Booking> filteredExpenses = filterByExpenditureType(expenses, expenditureType);

        return sum(filteredExpenses);
    }

    public Double sum(List<Booking> expenses) {
        double sum = 0D;

        for (Booking expense : expenses) {
            sum += expense.getPrice().getPrice();
        }

        return sum;
    }

    public HashMap<Integer, Double> byYear(List<Booking> bookings) {
        HashMap<Integer, List<Booking>> groupedExpenses = groupByYear(bookings);

        HashMap<Integer, Double> summedExpenses = new HashMap<>();
        for (Map.Entry<Integer, List<Booking>> entry : groupedExpenses.entrySet()) {
            summedExpenses.put(
                    entry.getKey(),
                    sum(entry.getValue())
            );
        }

        return summedExpenses;
    }

    private List<Booking> filterByExpenditureType(List<Booking> expenses, boolean expenditureType) {
        return new ExpenseFilter().byExpenditureType(expenses, expenditureType);
    }

    private HashMap<Category, List<Booking>> groupByCategory(List<Booking> expenses) {
        return new ExpenseGrouper().byCategory(expenses);
    }

    private HashMap<Integer, List<Booking>> groupByYear(List<Booking> expenses) {
        return new ExpenseGrouper().byYears(expenses);
    }
}
