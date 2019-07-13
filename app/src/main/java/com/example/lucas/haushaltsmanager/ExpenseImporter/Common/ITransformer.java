package com.example.lucas.haushaltsmanager.ExpenseImporter.Common;

import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.ExpenseImporter.Common.ImportableEntities.Common.ImportableEntityInterface;

public interface ITransformer {
    ExpenseObject transform(ILine line);

    void setExplodeStrategy(IExplodeStrategy strategy);

    /**
     * Entities, welche in der ILine enthalten werden müssen.
     *
     * @param entity
     */
    void registerEntity(ImportableEntityInterface entity);
}
