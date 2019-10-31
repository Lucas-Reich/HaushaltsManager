package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceParser.RequiredFields;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.R;

public final class Type implements IRequiredField {
    @Override
    public int getTranslationKey() {
        return R.string.mapping_price_type;
    }
}
