package com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ImportableEntities.Account;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ImportableEntities.Common.Field;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ImportableEntities.Common.ImportableEntityInterface;

import java.util.ArrayList;
import java.util.List;

public class ImportableAccount implements ImportableEntityInterface {
    @Override
    public List<Field> getRequiredFields() {
        List<Field> fields = new ArrayList<>();
        fields.add(new Balance());

        return fields;
    }

    @Override
    public List<Field> getOptionalFields() {
        return null;
    }
}
