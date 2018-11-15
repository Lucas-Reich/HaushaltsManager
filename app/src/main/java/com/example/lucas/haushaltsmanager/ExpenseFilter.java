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

    public List<ExpenseObject> byAccountWithChildren(List<ExpenseObject> expenses, List<Long> accounts) {
        return byAccount(pullChildrenUp(expenses), accounts);
    }

    public List<ExpenseObject> byAccount(List<ExpenseObject> expenses, List<Long> accounts) {
        List<ExpenseObject> filteredExpenses = new ArrayList<>();

        for (ExpenseObject expense : expenses) {
            if (hasAccount(expense, accounts) && !expense.isParent())
                filteredExpenses.add(expense);
        }

        return filteredExpenses;
    }

    private List<ExpenseObject> pullChildrenUp(List<ExpenseObject> expenses) {
        List<ExpenseObject> extractedExpenses = new ArrayList<>();

        for (ExpenseObject expense : expenses) {
            if (expense.isParent())
                extractedExpenses.addAll(expense.getChildren());
            else
                extractedExpenses.add(expense);
        }

        return extractedExpenses;
    }

    private boolean hasAccount(ExpenseObject expense, List<Long> accounts) {
        return accounts.contains(expense.getAccountId());
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
