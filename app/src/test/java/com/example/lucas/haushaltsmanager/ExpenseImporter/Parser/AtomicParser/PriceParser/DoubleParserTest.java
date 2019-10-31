package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DoubleParserTest {
    private DoubleParser parser;

    @Before
    public void setUp() {
        parser = new DoubleParser();
    }

    @Test
    public void canParseDoubleValue() {
        double expectedResult = 100D;

        Double actualResult = parser.parse(Double.toString(expectedResult));

        assertEquals(expectedResult, actualResult, 0);

    }

    @Test
    public void throwsExceptionForInvalidInput() {
        try {
            parser.parse("invalid_input");
        } catch (NumberFormatException e) {
            assertEquals("For input string: \"invalid_input\"", e.getMessage());
        }
    }
}
