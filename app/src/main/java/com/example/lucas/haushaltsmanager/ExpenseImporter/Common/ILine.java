package com.example.lucas.haushaltsmanager.ExpenseImporter.Common;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ImportableEntities.Common.Field;

public interface ILine {
    // Die Linie muss das mapping von index zu Feld haben
    String getLine();

    int getLineNumber();

    void fill(Field fielName);
}