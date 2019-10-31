package com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.AtomicParser.CategoryParser.RequiredFields;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Parser.IRequiredField;
import com.example.lucas.haushaltsmanager.R;

public final class Title implements IRequiredField {

    @Override
    public int getTranslationKey() {
        return R.string.mapping_category_title;
    }
}