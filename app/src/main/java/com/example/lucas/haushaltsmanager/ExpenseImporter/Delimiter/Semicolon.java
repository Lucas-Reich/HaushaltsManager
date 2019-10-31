package com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter;

public class Semicolon implements IDelimiter {
    @Override
    public String getDelimiter() {
        return ";";
    }
}
