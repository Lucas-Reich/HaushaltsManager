package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser.RequiredFields;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.R;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TypeTest {
    @Test
    public void returnsExpectedTranslationKey() {
        IRequiredField field = new Type();

        assertEquals(R.string.mapping_price_type, field.getTranslationKey());
    }
}
