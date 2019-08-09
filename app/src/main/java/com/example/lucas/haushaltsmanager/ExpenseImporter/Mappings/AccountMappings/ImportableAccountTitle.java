package com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.AccountMappings;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.ObjectParser.AccountParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.KeyMappingInterface;

public class ImportableAccountTitle implements KeyMappingInterface {
    private final String mapping;

    public ImportableAccountTitle(String mapping) {
        this.mapping = mapping;
    }

    @Override
    public String getKey() {
        return AccountParser.ACCOUNT_TITLE_KEY;
    }

    @Override
    public String getMappedField() {
        return mapping;
    }
}
