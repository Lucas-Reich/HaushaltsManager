package com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.BookingMappings;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.ObjectParser.BookingParser;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings.KeyMappingInterface;

public class ImportableBookingTitle implements KeyMappingInterface {
    private final String mapping;

    public ImportableBookingTitle(String mapping) {

        this.mapping = mapping;
    }

    @Override
    public String getKey() {
        return BookingParser.BOOKING_TITLE_KEY;
    }

    @Override
    public String getMappedField() {
        return mapping;
    }
}
