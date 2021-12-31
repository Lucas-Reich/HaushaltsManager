package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceTypeParser.RequiredFields;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.R;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PriceTypeTest {
    @Test
    public void returnsExpectedTranslationKey() {
        IRequiredField field = new PriceType();

        assertEquals(R.string.mapping_price_type, field.getTranslationKey());
    }
}
