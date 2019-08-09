package com.example.lucas.haushaltsmanager.ExpenseImporter.Line;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.DelimiterInterface;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidLineException;

import java.util.HashMap;

public class Line {
    private HashMap<String, String> fields = new HashMap<>();

    public Line(String header, String line, DelimiterInterface delimiter) throws InvalidLineException {
        String[] keys = header.split(delimiter.getDelimiter());
        String[] values = line.split(delimiter.getDelimiter(), -1);

        assertLessKeysThanFields(keys, values);

        for (int i = 0; i < keys.length; i++) {
            fields.put(keys[i], values[i]);
        }
    }

    public String getAsString(String key) {
        if (fields.containsKey(key)) {
            return fields.get(key);
        }

        return "";
    }

    private void assertLessKeysThanFields(String[] keys, String[] fields) throws InvalidLineException {
        int keyCount = keys.length;
        int fieldCount = fields.length;

        if (keyCount > fieldCount) {
            throw InvalidLineException.withInvalidEntryCount(keyCount, fieldCount);
        }
    }
}
