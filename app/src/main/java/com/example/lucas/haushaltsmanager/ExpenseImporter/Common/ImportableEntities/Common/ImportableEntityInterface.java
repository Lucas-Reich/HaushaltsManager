package com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ImportableEntities.Common;

import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ImportableEntities.Common.Field;

import java.util.List;

public interface ImportableEntityInterface {
    List<Field> getRequiredFields();

    List<Field> getOptionalFields();
}
