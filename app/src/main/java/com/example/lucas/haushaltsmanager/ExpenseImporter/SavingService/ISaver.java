package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService;

import com.example.lucas.haushaltsmanager.Entities.Account;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

public interface ISaver {
    void revert();

    void finish();

    void persist(ExpenseObject booking, Account account);
}
