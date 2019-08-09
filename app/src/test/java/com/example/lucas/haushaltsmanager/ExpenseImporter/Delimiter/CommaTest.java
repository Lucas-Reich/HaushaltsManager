package com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CommaTest {
    @Test
    public void shouldReturnCommaAsDelimiter() {
        DelimiterInterface delimiter = new Comma();

        assertEquals(",", delimiter.getDelimiter());
    }
}
