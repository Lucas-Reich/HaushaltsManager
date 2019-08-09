package com.example.lucas.haushaltsmanager.ExpenseImporter.Files;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Comma;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.DelimiterInterface;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Delimiter.Semicolon;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class DelimiterIdentifierTest {
    private DelimiterIdentifier delimiterIdentifier;

    @Before
    public void setUp() {
        delimiterIdentifier = new DelimiterIdentifier();
    }

    @Test
    public void correctlyHandlesNoDelimiter() {
        String stringWithDelimiter = generateStringWithDelimiter("");

        DelimiterInterface actualDelimiter = delimiterIdentifier.identifyDelimiter(stringWithDelimiter);

        assertSameInstance(actualDelimiter, DelimiterIdentifier.DEFAULT_DELIMITER);
    }

    @Test
    public void correctlyHandlesSameDelimiterCount() {
        DelimiterInterface expectedDelimiter = new Comma();
        String stringWithDelimiter = String.join(expectedDelimiter.getDelimiter(), "1;", "2;", "3;", "4;", "5;", "6;", "7;", "8;", "9");

        DelimiterInterface actualDelimiter = delimiterIdentifier.identifyDelimiter(stringWithDelimiter);

        assertSameInstance(actualDelimiter, expectedDelimiter);
    }

    @Test
    public void correctlyHandlesNotSupportedDelimiter() {
        String notSupportedDelimiter = "|";
        String stringWithDelimiter = generateStringWithDelimiter(notSupportedDelimiter);

        DelimiterInterface actualDelimiter = delimiterIdentifier.identifyDelimiter(stringWithDelimiter);

        assertSameInstance(actualDelimiter, DelimiterIdentifier.DEFAULT_DELIMITER);
    }

    @Test
    public void correctlyIdentifiesCommaAsDelimiter() {
        DelimiterInterface expectedDelimiter = new Comma();
        String stringWithDelimiter = generateStringWithDelimiter(expectedDelimiter.getDelimiter());

        DelimiterInterface actualDelimiter = delimiterIdentifier.identifyDelimiter(stringWithDelimiter);

        assertSameInstance(actualDelimiter, expectedDelimiter);
    }

    @Test
    public void correctlyIdentifiesSemicolonAsDelimiter() {
        DelimiterInterface expectedDelimiter = new Semicolon();
        String stringWithDelimiter = generateStringWithDelimiter(expectedDelimiter.getDelimiter());

        DelimiterInterface actualDelimiter = delimiterIdentifier.identifyDelimiter(stringWithDelimiter);

        assertSameInstance(actualDelimiter, expectedDelimiter);
    }

    private String generateStringWithDelimiter(String delimiter) {
        return String.join(delimiter, "1", "2", "3", "4", "5", "6", "7", "8", "9");
    }

    private void assertSameInstance(Object actual, Object expected) {
        assertEquals(actual.getClass(), expected.getClass());
    }
}
