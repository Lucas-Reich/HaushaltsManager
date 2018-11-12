package com.example.lucas.haushaltsmanager;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ExpenseGrouper {

    /**
     * Kindbuchungen werden von der Funktion nicht beachtet.
     *
     * @param month Started bei 1 (Januar) und endet bei 12 (Dezember).
     */
    public List<ExpenseObject> byMonth(List<ExpenseObject> expenses, int month, int year) {
        List<ExpenseObject> groupedExpenses = new ArrayList<>();

        for (ExpenseObject expense : expenses) {
            if (isExpenseInMonth(expense, (month - 1), year))
                groupedExpenses.add(expense);
        }

        return groupedExpenses;
    }

    /**
     * Kindbuchungen werden von der Funktion nicht beachtet.
     */
    public List<ExpenseObject> byYear(List<ExpenseObject> expenses, int year) {
        List<ExpenseObject> groupedExpenses = new ArrayList<>();

        for (ExpenseObject expense : expenses) {
            if (isExpenseInYear(expense, year))
                groupedExpenses.add(expense);
        }

        return groupedExpenses;
    }

    /**
     * Kindbuchungen werden von der Funktion nicht beachtet.
     */
    public HashMap<Category, List<ExpenseObject>> byCategory(List<ExpenseObject> expenses) {
        HashMap<Category, List<ExpenseObject>> groupedExpenses = new HashMap<>();

        for (final ExpenseObject expense : expenses) {
            Category expenseCategory = expense.getCategory();

            if (!groupedExpenses.containsKey(expenseCategory))
                groupedExpenses.put(expenseCategory, new ArrayList<ExpenseObject>());

            groupedExpenses.get(expenseCategory).add(expense);
        }

        return groupedExpenses;
    }

    private boolean isExpenseInMonth(ExpenseObject expense, int month, int year) {
        return expense.getDateTime().get(Calendar.MONTH) == month
                && isExpenseInYear(expense, year);
    }

    private boolean isExpenseInYear(ExpenseObject expense, int year) {
        return expense.getDateTime().get(Calendar.YEAR) == year;
    }
}
