package com.example.lucas.haushaltsmanager.ExpenseImporter.Common;

import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

public interface IDatabaseInserter {
    boolean insert(ExpenseObject expense);
}
