package com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CommaTest {
    @Test
    public void shouldReturnCommaAsDelimiter() {
        IDelimiter delimiter = new Comma();

        assertEquals(",", delimiter.getDelimiter());
    }
}
