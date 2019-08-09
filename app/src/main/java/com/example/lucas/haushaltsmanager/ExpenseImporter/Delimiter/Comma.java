package com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter;

public class Comma implements DelimiterInterface {
    @Override
    public String getDelimiter() {
        return ",";
    }
}
