package com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter;

public class Semicolon implements DelimiterInterface {
    @Override
    public String getDelimiter() {
        return ";";
    }
}
