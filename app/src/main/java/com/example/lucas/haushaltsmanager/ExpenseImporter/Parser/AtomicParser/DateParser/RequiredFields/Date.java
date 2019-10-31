package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DateParser.RequiredFields;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.R;

public final class Date implements IRequiredField {
    @Override
    public int getTranslationKey() {
        return R.string.mapping_booking_date;
    }
}
