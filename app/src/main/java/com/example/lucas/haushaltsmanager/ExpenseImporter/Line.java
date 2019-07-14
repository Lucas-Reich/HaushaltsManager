package com.example.lucas.haushaltsmanager.ExpenseImporter;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ILine;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ImportableEntities.Common.Field;

import java.util.HashMap;

public class Line implements ILine {
    private String line;
    private int lineNumber;
    private HashMap<Field, String> map;

    public Line(String line, int lineNumber, FieldMapping mapping) {
        this.line = line;
        this.lineNumber = lineNumber;

        map = new Mapper().createMapping(line, mapping);
    }

    @Override
    public String getLine() {
        // TODO: Brauche ich diese Funktion?
        return line;
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public void fill(Field field) throws IllegalArgumentException {
        guardAgainstNotExistingField(field);

        field.set(map.get(field));
    }

    private void guardAgainstNotExistingField(Field field) throws IllegalArgumentException {
        if (map.containsKey(field)) {
            return;
        }

        throw new IllegalArgumentException(String.format(
                "Line does not contain mapping for field %s",
                field.getClass().getSimpleName()
        ));
    }
}