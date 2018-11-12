package com.example.lucas.haushaltsmanager;

import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;

import java.util.ArrayList;
import java.util.List;

public class ExpenseFilter {

    public List<ExpenseObject> byExpenditureType(List<ExpenseObject> expenses, boolean filter) {
        List<ExpenseObject> filteredExpenses = new ArrayList<>();

        for (ExpenseObject expense : expenses) {
            if (expense.isExpenditure() == filter)
                filteredExpenses.add(expense);
        }

        return filteredExpenses;
    }
}
