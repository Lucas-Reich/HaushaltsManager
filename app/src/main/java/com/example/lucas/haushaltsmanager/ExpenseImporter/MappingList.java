package com.example.lucas.haushaltsmanager.ExpenseImporter;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.NoMappingFoundException;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;

import java.util.HashMap;

public class MappingList {
    private HashMap<IRequiredField, Integer> mappings;

    public MappingList() {
        mappings = new HashMap<>();
    }

    public void addMapping(IRequiredField key, int mappedValueIndex) {
        mappings.put(key, mappedValueIndex);
    }

    /**
     * @param key Key für den ein mapping erstellt werden soll.
     * @return Index des gemappten Feldes
     * @throws NoMappingFoundException Wenn der key nicht existiert, wird eine Exception ausgelöst
     */
    public int getMappingForKey(IRequiredField key) throws NoMappingFoundException {
        if (mappings.containsKey(key)) {
            return mappings.get(key);
        }

        throw NoMappingFoundException.withRequiredField(key);
    }
}