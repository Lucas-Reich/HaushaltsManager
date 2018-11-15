package com.example.lucas.haushaltsmanager.Entities.Report;

import com.example.lucas.haushaltsmanager.Entities.Currency;
import com.example.lucas.haushaltsmanager.Entities.ExpenseObject;
import com.example.lucas.haushaltsmanager.ExpenseGrouper;

import java.util.List;

public class Month extends AbstractReport {

    public Month(
            int month,
            int year,
            List<ExpenseObject> expenses,
            Currency currency
    ) {
        super(
                String.valueOf(month),
                expenses,
                currency
        );
    }

    @Override
    protected List<ExpenseObject> filterExpenses(List<ExpenseObject> expenses) {
        int month = 0; // TODO: Wie bekomme ich die Werte von dem Konstruktor hierhien
        int year = 0; // TODO: Wie bekomme ich die Werte von dem Konstruktor hierhien

        // TODO: Wie bekomme ich die Buchungen aussortieren, welche nicht benötigt werden
        return new ExpenseGrouper().byMonth(
                expenses,
                month,
                year
        );
    }
}
