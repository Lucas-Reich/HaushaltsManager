package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser.RequiredFields;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.R;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ValueTest {
    @Test
    public void returnsExpectedTranslationKey() {
        IRequiredField field = new Value();

        assertEquals(R.string.mapping_price_value, field.getTranslationKey());
    }
}
