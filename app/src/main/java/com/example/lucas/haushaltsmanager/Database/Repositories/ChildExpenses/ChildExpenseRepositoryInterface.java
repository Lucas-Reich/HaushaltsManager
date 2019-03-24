package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

import com.example.lucas.haushaltsmanager.Database.Repositories.Bookings.Exceptions.ExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.ChildExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

import java.util.List;

public interface ChildExpenseRepositoryInterface {
    ExpenseObject create(ExpenseObject child, ExpenseObject parent) throws ExpenseNotFoundException;

    void update(ExpenseObject child) throws ChildExpenseNotFoundException;

    void delete(ExpenseObject child);

    ExpenseObject get(long id) throws ChildExpenseNotFoundException;

    boolean exists(ExpenseObject child);

    void hide(ExpenseObject child) throws ChildExpenseNotFoundException;

    boolean isHidden(ExpenseObject child) throws ChildExpenseNotFoundException;

    List<ExpenseObject> getAll(long parentId);
}
