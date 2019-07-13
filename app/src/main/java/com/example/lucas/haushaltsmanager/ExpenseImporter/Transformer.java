package com.example.lucas.haushaltsmanager.ExpenseImporter;

import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.IExplodeStrategy;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ILine;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ITransformer;
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
        return null;
    }

    @Override
    public void setExplodeStrategy(IExplodeStrategy strategy) {
        explodeStrategy = strategy;
    }

    @Override
    public void registerEntity(ImportableEntityInterface entity) {
        entitiesToImport.add(entity);
    }
}
