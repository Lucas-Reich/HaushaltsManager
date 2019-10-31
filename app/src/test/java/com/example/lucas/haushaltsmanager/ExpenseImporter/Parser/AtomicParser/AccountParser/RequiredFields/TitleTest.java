package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.AccountParser.RequiredFields;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.R;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TitleTest {
    @Test
    public void returnsExpectedTranslationKey() {
        IRequiredField field = new Title();

        assertEquals(R.string.mapping_account_title, field.getTranslationKey());
    }
}
