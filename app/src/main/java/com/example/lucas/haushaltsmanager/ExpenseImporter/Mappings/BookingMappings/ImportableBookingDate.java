package com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.BookingMappings;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.ObjectParser.DateParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.KeyMappingInterface;

public class ImportableBookingDate implements KeyMappingInterface {
    private final String mapping;

    public ImportableBookingDate(String mapping) {
        this.mapping = mapping;
    }

    @Override
    public String getKey() {
        return DateParser.BOOKING_DATE_KEY;
    }

    @Override
    public String getMappedField() {
        return mapping;
    }
}
