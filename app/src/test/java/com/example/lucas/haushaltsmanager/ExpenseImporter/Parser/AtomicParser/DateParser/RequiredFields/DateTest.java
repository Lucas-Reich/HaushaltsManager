package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DateParser.RequiredFields;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.R;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DateTest {
    @Test
    public void returnsExpectedTranslationKey() {
        IRequiredField field = new Date();

        assertEquals(R.string.mapping_booking_date, field.getTranslationKey());
    }
}
