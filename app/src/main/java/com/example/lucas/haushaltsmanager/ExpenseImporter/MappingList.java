package com.example.lucas.haushaltsmanager.ExpenseImporter;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.KeyMappingInterface;

import java.util.ArrayList;
import java.util.List;

public class MappingList {
    private List<KeyMappingInterface> mappings;

    public MappingList() {
        mappings = new ArrayList<>();
    }

    public void addMapping(KeyMappingInterface mapping) {
        mappings.add(mapping);
    }

    public String getMappingForKey(String key) throws NoMappingFoundException {
        for (KeyMappingInterface keyMapping : mappings) {
            if (keyMapping.getKey().equals(key)) {
                return keyMapping.getMappedField();
            }
        }

        throw NoMappingFoundException.withKey(key);
    }
}