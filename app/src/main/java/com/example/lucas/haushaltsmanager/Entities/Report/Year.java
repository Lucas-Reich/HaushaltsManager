package com.example.lucas.haushaltsmanager.Entities.Report;

import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.ExpenseGrouper;

import java.util.List;

public class Year extends AbstractReport {
    private int year;

    private Year(String title, List<ExpenseObject> expenses, Currency currency) {
        super(
                title,
                expenses,
                currency
        );
    }

    public static Year create(int year, String title, List<ExpenseObject> expenses, Currency currency) {
        Year self = new Year(title, expenses, currency);
        self.year = year;

        return self;
    }

    @Override
    protected List<ExpenseObject> filterExpenses(List<ExpenseObject> expenses) {
        return new ExpenseGrouper().byYear(
                expenses,
                year
        );
    }
}
