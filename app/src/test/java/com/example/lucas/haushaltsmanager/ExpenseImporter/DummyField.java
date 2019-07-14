package com.example.lucas.haushaltsmanager.ExpenseImporter;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ImportableEntities.Common.Field;

public class DummyField implements Field {
    @Override
    public void validate(String field) {

    }

    @Override
    public Object getFieldClass() {
        return null;
    }

    @Override
    public void set(String value) {

    }
}
