package com.example.lucas.haushaltsmanager.ExpenseImporter;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;

import java.util.HashMap;

public class MappingList {
    private final HashMap<IRequiredField, Integer> mappings;

    public MappingList() {
        mappings = new HashMap<>();
    }

    public void addMapping(@NonNull IRequiredField key, int mappedValueIndex) {
        mappings.put(key, mappedValueIndex);
    }

    public int getMappingForKey(@NonNull IRequiredField key) throws NoMappingFoundException {
        if (mappings.containsKey(key)) {
            return mappings.get(key);
        }

        throw NoMappingFoundException.forRequiredField(key);
    }
}