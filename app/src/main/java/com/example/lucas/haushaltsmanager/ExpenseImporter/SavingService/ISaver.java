package com.example.lucas.haushaltsmanager.ExpenseImporter.SavingService;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Line.Line;

public interface ISaver {
    boolean save(Line line);

    void revert();

    void finish();
}
