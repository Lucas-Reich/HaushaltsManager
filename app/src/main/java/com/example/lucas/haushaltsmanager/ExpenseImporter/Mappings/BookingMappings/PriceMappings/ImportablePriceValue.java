package com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.BookingMappings.PriceMappings;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.ObjectParser.PriceParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.KeyMappingInterface;

public class ImportablePriceValue implements KeyMappingInterface {
    private final String mappedField;

    public ImportablePriceValue(String mappedField) {
        this.mappedField = mappedField;
    }

    @Override
    public String getKey() {
        return PriceParser.PRICE_VALUE_KEY;
    }

    @Override
    public String getMappedField() {
        return mappedField;
    }
}