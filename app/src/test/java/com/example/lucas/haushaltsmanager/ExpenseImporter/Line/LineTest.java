package com.example.lucas.haushaltsmanager.ExpenseImporter.Line;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Exception.InvalidLineException;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LineTest {
    @Test
    public void lineCanBeCreatedFromFilledInputFields() {
        String header = "1,2,3,4,5";
        String input = "31-12-2015,0,Berichtigung Kontostand,51.89,Girokonto Sparda";

        try {
            new Line(header, input, new Comma());
        } catch (InvalidLineException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void lineCanBeCreatedFromPartiallyFilledInputFields() {
        String header = "1,2,3,4,5";
        String input = "31-12-2015,0,,51.89,";

        try {
            new Line(header, input, new Comma());
        } catch (InvalidLineException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void lineCanBeCreatedFromEmptyInputFields() {
        String header = "1,2,3,4,5";
        String input = ",,,,";

        try {
            new Line(header, input, new Comma());
        } catch (InvalidLineException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void cannotCreateLineFromLessFieldsThanHeader() {
        String header = "1,2,3,4,5";
        String input = "1,2,3,4";

        try {
            new Line(header, input, new Comma());

            Assert.fail("Could create Line with mismatching header and field count.");
        } catch (InvalidLineException e) {

            assertEquals("Could not import malformed line. Expected 5 arguments, got 4 arguments.", e.getMessage());
        }
    }

    @Test
    public void canCreateLineFromMoreFieldsThanHeader() {
        String header = "1,2,3,4,5";
        String input = "1,2,3,4,5,6";

        try {
            new Line(header, input, new Comma());
        } catch (InvalidLineException e) {
            Assert.fail("Could not create Line from valid input");
        }
    }

    @Test
    public void getAsStringReturnsStringValue() {
        String header = "1,2,3,4,5";
        String input = "31-12-2015,0,Berichtigung Kontostand,51.89,";

        Line line = new Line(header, input, new Comma());

        assertEquals(String.class, line.getAsString("3").getClass());
        assertEquals("Berichtigung Kontostand", line.getAsString("3"));
    }

    @Test
    public void getAsStringReturnsDefaultValueForNotExistingField() {
        String header = "1,2,3,4,5";
        String input = "1,2,3,4,5";

        Line line = new Line(header, input, new Comma());
        String fieldValue = line.getAsString("notExistingKey");

        assertEquals("", fieldValue);
    }
}
