package com.example.lucas.haushaltsmanager.ExpenseImporter.Line;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.IDelimiter;

public class Line {
    private String[] values;

    public Line(String line, IDelimiter delimiter) {
        values = line.split(delimiter.getDelimiter(), -1);
    }

    public String getAsString(int index) {
        if (index < values.length) {
            return values[index];
        }

        return ""; // TODO: Sollte ich hier eine Exception auslösen oder vielleicht mit einem default Wert arbeiten?
    }
}
