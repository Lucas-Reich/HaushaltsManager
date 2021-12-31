package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.PriceTypeParser.RequiredFields;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.R;

public final class PriceType implements IRequiredField {
    @Override
    public int getTranslationKey() {
        return R.string.mapping_price_type;
    }
}
