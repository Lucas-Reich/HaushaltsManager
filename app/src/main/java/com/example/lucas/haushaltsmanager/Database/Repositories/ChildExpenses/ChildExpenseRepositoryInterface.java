package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.AddChildToChildException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.CannotDeleteChildExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.ChildExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;
import com.example.lucas.haushaltsmanager.Entities.Expense.ParentBooking;

import java.util.List;
import java.util.UUID;

public interface ChildExpenseRepositoryInterface {
    boolean exists(ExpenseObject childExpense);

    ExpenseObject addChildToBooking(ExpenseObject childExpense, ExpenseObject parentBooking) throws AddChildToChildException;

    ExpenseObject extractChildFromBooking(ExpenseObject childExpense) throws ChildExpenseNotFoundException;

    ExpenseObject get(UUID childExpenseId) throws ChildExpenseNotFoundException;

    List<ExpenseObject> getAll(UUID parentExpenseId);

    void insert(ParentBooking parent, ExpenseObject child);

    void insert(ExpenseObject parentExpense, ExpenseObject childExpense);

    void update(ExpenseObject childExpense) throws ChildExpenseNotFoundException;

    void delete(ExpenseObject childExpense) throws CannotDeleteChildExpenseException;

    void hide(ExpenseObject childExpense) throws ChildExpenseNotFoundException;

    void closeDatabase();
}
