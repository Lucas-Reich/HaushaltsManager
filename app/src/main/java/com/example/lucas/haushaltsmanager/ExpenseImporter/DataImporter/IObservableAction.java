package com.example.lucas.haushaltsmanager.ExpenseImporter.DataImporter;

import com.example.lucas.haushaltsmanager.ExpenseImporter.ISub;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;

import java.util.List;

public interface IObservableAction {
    void addSub(ISub sub);

    void removeSub(ISub sub);

    int totalSteps();

    List<IRequiredField> getRequiredFields();
}
