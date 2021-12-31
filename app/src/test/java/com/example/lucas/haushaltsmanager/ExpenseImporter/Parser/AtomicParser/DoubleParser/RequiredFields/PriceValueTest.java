package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DoubleParser.RequiredFields;

import static org.junit.Assert.assertEquals;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.R;

import org.junit.Test;

public class PriceValueTest {
    @Test
    public void returnsExpectedTranslationKey() {
        IRequiredField field = new PriceValue();

        assertEquals(R.string.mapping_price_value, field.getTranslationKey());
    }
}
