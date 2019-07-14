package com.example.lucas.haushaltsmanager.ExpenseImporter;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ImportableEntities.Common.Field;

import java.util.HashMap;

public class FieldMapping {
    private HashMap<Field, Integer> mapping = new HashMap<>();

    public FieldMapping() {

    }

    public void createMapping(Field field, int index) {
        mapping.put(field, index);
    }

    public HashMap<Field, Integer> getMapping() {
        return mapping;
    }
}