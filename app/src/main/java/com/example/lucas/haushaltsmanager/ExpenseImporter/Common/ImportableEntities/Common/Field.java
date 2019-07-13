package com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ImportableEntities.Common;

public interface Field {
    void validate(String field);

    Object getFieldClass();

//    String getDatabaseField(); // TODO: Kann ich das gebrauchen? Hier würde dann TABLE_COL_... returned
}