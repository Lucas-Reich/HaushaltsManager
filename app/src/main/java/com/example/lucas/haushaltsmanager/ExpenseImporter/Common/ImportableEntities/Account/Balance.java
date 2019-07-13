package com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ImportableEntities.Account;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ImportableEntities.Common.Field;

public class Balance implements Field {
    @Override
    public void validate(String field) {

    }

    @Override
    public Object getFieldClass() {
        return Double.class;
    }
}
