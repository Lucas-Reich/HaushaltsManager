package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.BookingParser.RequiredFields;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.R;

public final class BookingTitle implements IRequiredField {
    @Override
    public int getTranslationKey() {
        return R.string.mapping_booking_title;
    }
}
