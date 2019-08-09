package com.example.lucas.haushaltsmanager.ExpenseImporter.Mappings;

public interface KeyMappingInterface {
    String getKey();

    String getMappedField();

    // TODO: Ich müsste noch einen Weg einbauen mit dem sichergestellt werden kann,
    //  dass der zu importierende String auch den Vorgaben des Objekts entspricht
    //  (nicht leer, bestimmte anzahl an Zeichen, ...)
}
