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
     */
    // TODO: Kann ich die Funktion durch byYears() ersetzen?
    public List<ExpenseObject> byYear(List<ExpenseObject> expenses, int year) {
        List<ExpenseObject> groupedExpenses = new ArrayList<>();

        for (ExpenseObject expense : expenses) {
            if (isInYear(expense, year))
                groupedExpenses.add(expense);
        }

        return groupedExpenses;
    }

    public HashMap<Integer, List<ExpenseObject>> byYears(List<ExpenseObject> expenses) {
        HashMap<Integer, List<ExpenseObject>> groupedExpenses = new HashMap<>();

        for (ExpenseObject expense : expenses) {
            int expenseYear = expense.getDateTime().get(Calendar.YEAR);

            if (!groupedExpenses.containsKey(expenseYear))
                groupedExpenses.put(expenseYear, new ArrayList<ExpenseObject>());

            groupedExpenses.get(expenseYear).add(expense);
        }

        return groupedExpenses;
    } // TODO: Tests schreiben

    /**
     * Kindbuchungen werden von der Funktion nicht beachtet.
     */
    public HashMap<Category, List<ExpenseObject>> byCategory(List<ExpenseObject> expenses) {
        HashMap<Category, List<ExpenseObject>> groupedExpenses = new HashMap<>();

        for (ExpenseObject expense : expenses) {
            Category expenseCategory = expense.getCategory();

            if (!groupedExpenses.containsKey(expenseCategory))
                groupedExpenses.put(expenseCategory, new ArrayList<ExpenseObject>());

            groupedExpenses.get(expenseCategory).add(expense);
        }

        return groupedExpenses;
    }

    public List<ExpenseObject> byMonth(List<ExpenseObject> expenses, int month, int year) {
        List<ExpenseObject> groupedExpenses = new ArrayList<>();

        for (ExpenseObject expense : expenses) {
            if (isInMonth(expense, month) && isInYear(expense, year))
                groupedExpenses.add(expense);
        }

        return groupedExpenses;
    }

    public List<List<ExpenseObject>> byMonths(List<ExpenseObject> expenses, int year) {
        List<List<ExpenseObject>> groupedExpenses = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            groupedExpenses.add(new ArrayList<ExpenseObject>());
        }

        for (ExpenseObject expense : expenses) {
            if (!isInYear(expense, year))
                continue;

            groupedExpenses.get(extractMonth(expense)).add(expense);
        }

        return groupedExpenses;
    }

    private int extractMonth(ExpenseObject expense) {
        return expense.getDateTime().get(Calendar.MONTH);
    }

    private boolean isInMonth(ExpenseObject expense, int month) {
        return expense.getDateTime().get(Calendar.MONTH) == month;
    }

    private boolean isInYear(ExpenseObject expense, int year) {
        return expense.getDateTime().get(Calendar.YEAR) == year;
    }
}
