package com.example.lucas.haushaltsmanager.Utils.ExpenseUtils;

import com.example.lucas.haushaltsmanager.Entities.Category;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseSum {

    public HashMap<Category, Double> byCategory(List<ExpenseObject> expenses) {
        HashMap<Category, List<ExpenseObject>> expensesGroupedByCategory = groupByCategory(pullChildrenUp(expenses));

        HashMap<Category, Double> summedExpenses = new HashMap<>();
        for (Map.Entry<Category, List<ExpenseObject>> entry : expensesGroupedByCategory.entrySet()) {
            summedExpenses.put(
                    entry.getKey(),
                    sum(entry.getValue())
            );
        }

        return summedExpenses;
    }

    public double byExpenditureType(boolean expenditureType, List<ExpenseObject> expenses) {
        List<ExpenseObject> filteredExpenses = pullChildrenUp(expenses);

        filteredExpenses = filterByExpenditureType(filteredExpenses, expenditureType);

        return sum(filteredExpenses);
    }

    public double byMonth(List<ExpenseObject> expenses, int month, int year) {
        List<ExpenseObject> expensesInMonth = pullChildrenUp(expenses);

        expensesInMonth = filterByMonth(expensesInMonth, month, year);

        return sum(expensesInMonth);
    }

    public Double sum(List<ExpenseObject> expenses) {
        double sum = 0D;

        for (ExpenseObject expense : expenses) {
            sum += expense.getSignedPrice();
        }

        return sum;
    }

    public HashMap<Integer, Double> byYear(List<ExpenseObject> expenses) {
        HashMap<Integer, List<ExpenseObject>> groupedExpenses = groupByYear(pullChildrenUp(expenses));

        HashMap<Integer, Double> summedExpenses = new HashMap<>();
        for (Map.Entry<Integer, List<ExpenseObject>> entry : groupedExpenses.entrySet()) {
            summedExpenses.put(
                    entry.getKey(),
                    sum(entry.getValue())
            );
        }

        return summedExpenses;
    }

    private List<ExpenseObject> filterByExpenditureType(List<ExpenseObject> expenses, boolean expenditureType) {
        return new ExpenseFilter().byExpenditureType(expenses, expenditureType);
    }

    private List<ExpenseObject> filterByMonth(List<ExpenseObject> expenses, int month, int year) {
        return new ExpenseFilter().byMonth(expenses, month, year);
    }

    private HashMap<Category, List<ExpenseObject>> groupByCategory(List<ExpenseObject> expenses) {
        return new ExpenseGrouper().byCategory(expenses);
    }

    private HashMap<Integer, List<ExpenseObject>> groupByYear(List<ExpenseObject> expenses) {
        return new ExpenseGrouper().byYears(expenses);
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
}
