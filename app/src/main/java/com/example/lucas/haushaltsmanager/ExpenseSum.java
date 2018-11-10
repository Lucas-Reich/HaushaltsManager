package com.example.lucas.haushaltsmanager;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseSum {
    private ExpenseGrouper mExpenseGrouper;

    public ExpenseSum() {
        mExpenseGrouper = new ExpenseGrouper();
    }

    public HashMap<Category, Double> sumBookingsByCategory(List<ExpenseObject> expenses) {
        HashMap<Category, Double> summedExpenses = new HashMap<>();
        HashMap<Category, List<ExpenseObject>> expensesGroupedByCategory = mExpenseGrouper.groupByCategory(flattenBookings(expenses));

        for (Map.Entry<Category, List<ExpenseObject>> entry : expensesGroupedByCategory.entrySet()) {
            summedExpenses.put(
                    entry.getKey(),
                    getSum(entry.getValue())
            );
        }

        return summedExpenses;
    }

    private Double getSum(List<ExpenseObject> expenses) {
        Double sum = 0D;

        for (ExpenseObject expense : expenses) {
            sum += expense.getSignedPrice();
        }

        return sum;
    }

    /**
     * Funktion stellt sicher das keine Buchung mehr Kinder hat.
     * Falls eine Buchung mit Kindern gefunden wird,
     * dann werden diese extrahiert und der Buchungsliste hinzugefügt.
     */
    private List<ExpenseObject> flattenBookings(List<ExpenseObject> expenses) {
        List<ExpenseObject> extractedExpenses = new ArrayList<>();

        for (ExpenseObject expense : expenses) {
            if (expense.isParent())
                extractedExpenses.addAll(expense.getChildren());
            else
                extractedExpenses.add(expense);
        }

        return extractedExpenses;
    }
}
