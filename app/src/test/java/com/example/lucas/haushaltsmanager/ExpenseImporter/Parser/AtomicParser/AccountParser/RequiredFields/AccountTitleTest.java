package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.AccountParser.RequiredFields;

import static org.junit.Assert.assertEquals;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.R;

import org.junit.Test;

public class AccountTitleTest {
    @Test
    public void returnsExpectedTranslationKey() {
        // Act
        IRequiredField field = new AccountTitle();

        // Assert
        assertEquals(R.string.mapping_account_title, field.getTranslationKey());
    }
}
