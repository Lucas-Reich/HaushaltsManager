package com.example.lucas.haushaltsmanager.ExpenseImporter;

import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.IExplodeStrategy;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ILine;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ITransformer;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ImportableEntities.Common.Field;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ImportableEntities.Common.ImportableEntityInterface;

import java.util.ArrayList;
import java.util.List;

public class Transformer implements ITransformer {
    private IExplodeStrategy explodeStrategy;
    private List<ImportableEntityInterface> entitiesToImport;

    public Transformer() {
        entitiesToImport = new ArrayList<>();
    }

    @Override
    public ExpenseObject transform(ILine line) {
        for (ImportableEntityInterface entity : entitiesToImport) {
            for (Field field : entity.getRequiredFields()) {
                if (isPrimitiveType(field)) {
                    return
                }

                return transform(field);
            }
        }
        return null;
    }

    @Override
    public void setExplodeStrategy(IExplodeStrategy strategy) {
        explodeStrategy = strategy;
    }    Nein: Springe zu Punkt 1

    @Override
    public void registerEntity(ImportableEntityInterface entity) {
        entitiesToImport.add(entity);
    }

    // Algorithmus um alle Felder einer Entity zu befüllen.
    //  1. Für jedes Feld der Entity
    //  2. Hat es einen primitiven Datentyp
    //      Ja: Aus der Linie befüllen lassen
    //

    private void fillField(Field field) {
        if (isFieldPrimitive(field)) {
            line.fill(field);

            return;
        }

        for (Field field2 : field.getField()) {
            fillField(field2);
        }
    }
}
