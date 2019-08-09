package com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter;

import com.example.lucas.haushaltsmanager.ExpenseImporter.ISub;

public interface IObservableAction {
    void addSub(ISub sub);

    void removeSub(ISub sub);

    int totalSteps();
}
