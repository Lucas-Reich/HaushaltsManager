package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BooleanParserTest {
    private BooleanParser parser;

    @Before
    public void setUp() {
        parser = new BooleanParser();
    }

    @Test
    public void canParseTrueValues() {
        assertTrue(parser.parse("true"));

        assertTrue(parser.parse("1"));
    }

    @Test
    public void canParseFalseValues() {
        assertFalse(parser.parse("false"));

        assertFalse(parser.parse("0"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionForInvalidInput() {
        parser.parse("invalid_input");
    }
}
