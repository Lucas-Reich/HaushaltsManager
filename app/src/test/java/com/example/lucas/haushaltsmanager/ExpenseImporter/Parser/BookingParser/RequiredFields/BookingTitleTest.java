package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.BookingParser.RequiredFields;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.R;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BookingTitleTest {
    @Test
    public void returnsExpectedTranslationKey() {
        IRequiredField field = new BookingTitle();

        assertEquals(R.string.mapping_booking_title, field.getTranslationKey());
    }
}
