package com.example.lucas.haushaltsmanager.ExpenseImporter.Line;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LineTest {
    @Test
    public void lineCanBeCreatedFromFilledInputFields() {
        String input = "31-12-2015,0,Berichtigung Kontostand,51.89,Girokonto Sparda";

        new Line(input, new Comma());
    }

    @Test
    public void lineCanBeCreatedFromPartiallyFilledInputFields() {
        String input = "31-12-2015,0,,51.89,";

        new Line(input, new Comma());
    }

    @Test
    public void lineCanBeCreatedFromEmptyInputFields() {
        String input = ",,,,";

        new Line(input, new Comma());
    }

    @Test
    public void getAsStringReturnsStringValue() {
        String input = "31-12-2015,0,Berichtigung Kontostand,51.89,";

        Line line = new Line(input, new Comma());

        assertEquals("Berichtigung Kontostand", line.getAsString(2));
    }

    @Test
    public void getAsStringReturnsDefaultValueForNotExistingField() {
        String input = "1,2,3,4,5";

        Line line = new Line(input, new Comma());
        String fieldValue = line.getAsString(101);

        assertEquals("", fieldValue);
    }
}
