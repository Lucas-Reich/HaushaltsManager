package com.example.lucas.haushaltsmanager.ExpenseImporter.Common;

public interface ILine {
    // Die Linie muss das mapping von index zu Feld haben
    String getLine();

    String getFieldAsString(String fieldName);

    int getLineNumber();
}