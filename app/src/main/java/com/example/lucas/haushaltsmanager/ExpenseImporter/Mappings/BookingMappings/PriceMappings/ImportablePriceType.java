package com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.BookingMappings.PriceMappings;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.ObjectParser.PriceParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.KeyMappingInterface;

public class ImportablePriceType implements KeyMappingInterface {
    private final String mapping;

    public ImportablePriceType(String mapping) {
        this.mapping = mapping;
    }

    @Override
    public String getKey() {
        return PriceParser.PRICE_TYPE_KEY;
    }

    @Override
    public String getMappedField() {
        return mapping;
    }
}
