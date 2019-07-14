package com.example.lucas.haushaltsmanager.ExpenseImporter;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ImportableEntities.Common.Field;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidMappingException;

import java.util.HashMap;
import java.util.Map;

public class Mapper {
    public HashMap<Field, String> createMapping(String line, FieldMapping mapping) {
        String[] exploded = line.split(","); // TODO: Get delimiter from IExplodeStrategy

        guardAgainstTooFewFields(exploded, mapping.getMapping());

        return createMap(mapping.getMapping(), exploded);
    }

    private HashMap<Field, String> createMap(HashMap<Field, Integer> mapping, String[] line) {
        HashMap<Field, String> map = new HashMap<>();

        for (Map.Entry<Field, Integer> field : mapping.entrySet()) {
            String value = tryToGet(line, field.getValue());

            map.put(field.getKey(), value);
        }

        return map;
    }

    private String tryToGet(String[] fields, int index) throws InvalidMappingException {
        if (index > (fields.length - 1)) {
            throw InvalidMappingException.noValueFoundForIndex(index);
        }

        return fields[index];
    }

    private void guardAgainstTooFewFields(String[] fields, HashMap<Field, Integer> mapping) throws InvalidMappingException {
        if (fields.length < mapping.size()) {
            throw InvalidMappingException.tooFewFields(mapping.size(), fields.length);
        }
    }
}