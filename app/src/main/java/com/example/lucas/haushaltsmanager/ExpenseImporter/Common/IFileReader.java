package com.example.lucas.haushaltsmanager.ExpenseImporter.Common;

public interface IFileReader {
    ILine get(int lineNumber);

    ILine getCurrent();

    boolean moveToNext();

    int getCount();
}
