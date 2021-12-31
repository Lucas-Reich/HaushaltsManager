package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.DoubleParser.RequiredFields;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.R;

public final class PriceValue implements IRequiredField {
    @Override
    public int getTranslationKey() {
        return R.string.mapping_price_value;
    }
}