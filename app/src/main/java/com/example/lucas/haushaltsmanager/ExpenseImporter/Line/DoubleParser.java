package com.example.lucas.haushaltsmanager.ExpenseImporter.Line;

public class DoubleParser {
    // TODO: Sollte ich auch es auch möglich machen Doubles zu parsen, die "," Werte lesen können
    public Double parse(String input) throws NumberFormatException {
        return Double.valueOf(input);
    }
}
