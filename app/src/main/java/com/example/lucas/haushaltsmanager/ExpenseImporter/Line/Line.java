package com.example.lucas.haushaltsmanager.ExpenseImporter.Line;

import androidx.annotation.NonNull;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.IDelimiter;

public class Line {
    private final String[] values;

    public Line(String line, IDelimiter delimiter) {
        values = line.split(delimiter.getDelimiter(), -1);
    }

    @NonNull
    public String getAsString(int index) throws IndexOutOfBoundsException {
        if (index >= 0 && index < values.length) {
            return values[index];
        }

        throw new IndexOutOfBoundsException(String.format(
                "Could not retrieve value from index at position '%d'. Line only has '%d' values",
                index,
                values.length
        ));
    }
}
