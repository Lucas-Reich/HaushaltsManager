package com.example.lucas.haushaltsmanager;

import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExpenseFilter {

    public List<ExpenseObject> byExpenditureType(List<ExpenseObject> expenses, boolean filter) {
        List<ExpenseObject> filteredExpenses = new ArrayList<>();

        for (ExpenseObject expense : expenses) {
            if (matchesFilter(expense, filter) && !expense.isParent())
                filteredExpenses.add(expense);
        }

        return filteredExpenses;
    }

    public List<ExpenseObject> byMonth(List<ExpenseObject> expenses, int month, int year) {
        List<ExpenseObject> filteredExpenses = new ArrayList<>();

        for (ExpenseObject expense : expenses) {
            if (isInMonth(expense, month) && isInYear(expense, year) && !expense.isParent())
                filteredExpenses.add(expense);
        }

        return filteredExpenses;
    }

    private boolean isInMonth(ExpenseObject expense, int month) {
        return expense.getDateTime().get(Calendar.MONTH) == month;
    }

    private boolean isInYear(ExpenseObject expense, int year) {
        return expense.getDateTime().get(Calendar.YEAR) == year;
    }

    private boolean matchesFilter(ExpenseObject expense, boolean filter) {
        return expense.isExpenditure() == filter;
    }
}
