package com.example.lucas.haushaltsmanager.ExpenseImporter;

import java.util.HashMap;

public class ExpenseMap {
    public enum RequiredExpenseFields {
        TITLE,
        PRICE,
        ACCOUNT,
        CATEGORY
    }

    private HashMap<RequiredExpenseFields, String> map;

    public ExpenseMap() {
        map = new HashMap<>();
    }

    public void mapField(String fieldName, RequiredExpenseFields expenseField) {
        map.put(expenseField, fieldName);
    }

    public HashMap<RequiredExpenseFields, String> getMappedField() {
        return map;
    }
}
