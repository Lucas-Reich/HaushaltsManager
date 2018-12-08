package com.example.lucas.haushaltsmanager.Entities.Report;

import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.ExpenseGrouper;

import java.util.List;

public class Month extends AbstractReport {
    private int month;
    private int year;

    public Month(
            int month,
            List<ExpenseObject> expenses,
            Currency currency
    ) {
        super(
                String.valueOf(month),
                expenses,
                currency
        );
    }

    public static Month create(int month, int year, List<ExpenseObject> expenses, Currency currency) {
        Month self = new Month(month, expenses, currency);
        self.month = month;
        self.year = year;

        return self;
    }

    @Override
    protected List<ExpenseObject> filterExpenses(List<ExpenseObject> expenses) {
        // TODO: Wie bekomme ich die Buchungen aussortieren, welche nicht benötigt werden
        return new ExpenseGrouper().byMonth(
                expenses,
                this.month,
                this.year
        );
    }
}
