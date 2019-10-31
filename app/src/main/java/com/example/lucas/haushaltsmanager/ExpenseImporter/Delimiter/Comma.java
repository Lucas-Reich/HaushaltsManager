package com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter;

public class Comma implements IDelimiter {
    @Override
    public String getDelimiter() {
        return ",";
    }
}
