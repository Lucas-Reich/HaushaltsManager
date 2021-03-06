package com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses;

import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.AddChildToChildException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.CannotDeleteChildExpenseException;
import com.example.lucas.haushaltsmanager.Database.Repositories.ChildExpenses.Exceptions.ChildExpenseNotFoundException;
import com.example.lucas.haushaltsmanager.Entities.Expense.ExpenseObject;

import java.util.List;

public interface ChildExpenseRepositoryInterface {
    boolean exists(ExpenseObject childExpense);

    ExpenseObject addChildToBooking(ExpenseObject childExpense, ExpenseObject parentBooking) throws AddChildToChildException;

    ExpenseObject combineExpenses(List<ExpenseObject> expenses);

    ExpenseObject extractChildFromBooking(ExpenseObject childExpense) throws ChildExpenseNotFoundException;

    ExpenseObject get(long childExpenseId) throws ChildExpenseNotFoundException;

    List<ExpenseObject> getAll(long parentExpenseId);

    ExpenseObject insert(ExpenseObject parentExpense, ExpenseObject childExpense);

    void update(ExpenseObject childExpense) throws ChildExpenseNotFoundException;

    void delete(ExpenseObject childExpense) throws CannotDeleteChildExpenseException;

    void hide(ExpenseObject childExpense) throws ChildExpenseNotFoundException;

    void closeDatabase();
}
